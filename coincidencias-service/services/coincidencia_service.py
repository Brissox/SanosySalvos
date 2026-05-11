"""
Capa de negocio: orquesta BD, motor ML y publicación de notificaciones.
"""

import asyncio
import logging

from sqlalchemy.orm import Session

from cache.redis_client import get_client
from config import settings
from db.models import Coincidencia, EstadoCoincidencia, EstadoReporte, TipoReporte
from matching.engine import ResultadoMatch, ejecutar_matching
from messaging.publisher import publicar_actualizar_estado_reporte, publicar_notificacion
from schemas.schemas import (
    ActualizarEstadoDTO,
    CoincidenciaResponse,
    NotificacionCoincidenciaDTO,
    ReporteEventoDTO,
)

logger = logging.getLogger(__name__)

_TIPOS_OPUESTOS = {
    TipoReporte.PERDIDA: {TipoReporte.ENCONTRADA, TipoReporte.AVISTAMIENTO},
    TipoReporte.ENCONTRADA: {TipoReporte.PERDIDA},
    TipoReporte.AVISTAMIENTO: {TipoReporte.PERDIDA},
}


async def registrar_reporte_en_cache(reporte: ReporteEventoDTO) -> None:
    if reporte.estado_reporte in (EstadoReporte.RESUELTO, EstadoReporte.CERRADO):
        logger.info("Reporte %d ignorado en cache por estado %s", reporte.reporte_id, reporte.estado_reporte)
        return
    r = get_client()
    async with r.pipeline(transaction=True) as pipe:
        pipe.setex(
            f"reporte:{reporte.reporte_id}",
            settings.redis_ttl_reporte,
            reporte.model_dump_json(),
        )
        pipe.sadd(f"reportes:tipo:{reporte.tipo_reporte.value}", str(reporte.reporte_id))
        await pipe.execute()


async def _quitar_reporte_de_cache(reporte_id: int) -> None:
    r = get_client()
    cached = await r.get(f"reporte:{reporte_id}")
    if cached:
        reporte = ReporteEventoDTO.model_validate_json(cached)
        await r.srem(f"reportes:tipo:{reporte.tipo_reporte.value}", str(reporte_id))


async def _restaurar_reporte_en_cache(reporte_id: int) -> None:
    r = get_client()
    cached = await r.get(f"reporte:{reporte_id}")
    if cached:
        reporte = ReporteEventoDTO.model_validate_json(cached)
        await r.sadd(f"reportes:tipo:{reporte.tipo_reporte.value}", str(reporte_id))
    else:
        logger.warning("No se pudo restaurar reporte %d en cache: clave expirada", reporte_id)


def _tiene_coincidencias_activas(reporte_id: int, db: Session) -> bool:
    return (
        db.query(Coincidencia)
        .filter(
            (Coincidencia.reporte_perdido_id == reporte_id)
            | (Coincidencia.reporte_encontrado_id == reporte_id),
            Coincidencia.estado == EstadoCoincidencia.PENDIENTE,
        )
        .count()
        > 0
    )


async def aplicar_efectos_cambio_estado(
    coincidencia: "CoincidenciaResponse",
    nuevo_estado: EstadoCoincidencia,
    db: Session,
) -> None:
    perdido_id = coincidencia.reporte_perdido_id
    encontrado_id = coincidencia.reporte_encontrado_id

    if nuevo_estado == EstadoCoincidencia.CONFIRMADA:
        confirmados = {perdido_id, encontrado_id}

        pendientes = db.query(Coincidencia).filter(
            (
                (Coincidencia.reporte_perdido_id == perdido_id)
                | (Coincidencia.reporte_encontrado_id == perdido_id)
                | (Coincidencia.reporte_perdido_id == encontrado_id)
                | (Coincidencia.reporte_encontrado_id == encontrado_id)
            ),
            Coincidencia.estado == EstadoCoincidencia.PENDIENTE,
        ).all()

        # Reportes afectados colateralmente (no son los dos confirmados)
        colaterales: set[int] = set()
        for c in pendientes:
            if c.reporte_perdido_id not in confirmados:
                colaterales.add(c.reporte_perdido_id)
            if c.reporte_encontrado_id not in confirmados:
                colaterales.add(c.reporte_encontrado_id)

        for c in pendientes:
            c.estado = EstadoCoincidencia.DESCARTADA
        db.commit()

        # Revertir colaterales a ACTIVO si ya no tienen coincidencias pendientes
        for reporte_id in colaterales:
            if not _tiene_coincidencias_activas(reporte_id, db):
                await publicar_actualizar_estado_reporte(reporte_id, "ACTIVO")
                await _restaurar_reporte_en_cache(reporte_id)

        for reporte_id in confirmados:
            await publicar_actualizar_estado_reporte(reporte_id, "RESUELTO")
            await _quitar_reporte_de_cache(reporte_id)

    elif nuevo_estado == EstadoCoincidencia.DESCARTADA:
        for reporte_id in (perdido_id, encontrado_id):
            if not _tiene_coincidencias_activas(reporte_id, db):
                await publicar_actualizar_estado_reporte(reporte_id, "ACTIVO")
                await _restaurar_reporte_en_cache(reporte_id)


async def _candidatos_opuestos(nuevo: ReporteEventoDTO) -> list[ReporteEventoDTO]:
    r = get_client()
    tipos_opuestos = _TIPOS_OPUESTOS.get(nuevo.tipo_reporte, set())

    all_ids: set[str] = set()
    for tipo in tipos_opuestos:
        ids = await r.smembers(f"reportes:tipo:{tipo.value}")
        all_ids.update(ids)

    if not all_ids:
        return []

    # Obtener todos los reportes en un único round-trip
    valores = await r.mget(*[f"reporte:{id_}" for id_ in all_ids])

    candidatos = [ReporteEventoDTO.model_validate_json(v) for v in valores if v is not None]
    return [
        c for c in candidatos
        if c.tipo_reporte in tipos_opuestos and c.reporte_id != nuevo.reporte_id
    ]


async def procesar_reporte_nuevo(reporte: ReporteEventoDTO, db: Session) -> None:
    await registrar_reporte_en_cache(reporte)
    candidatos = await _candidatos_opuestos(reporte)

    if not candidatos:
        logger.info("Sin candidatos para reporte %d", reporte.reporte_id)
        return

    resultados: list[ResultadoMatch] = await asyncio.to_thread(
        ejecutar_matching, reporte, candidatos, db
    )

    ids_actualizados: set[int] = set()

    for r in resultados:
        coincidencia = (
            db.query(Coincidencia)
            .filter_by(
                reporte_perdido_id=r.reporte_perdido_id,
                reporte_encontrado_id=r.reporte_encontrado_id,
            )
            .order_by(Coincidencia.fecha_calculo.desc())
            .first()
        )
        if coincidencia:
            await publicar_notificacion(
                NotificacionCoincidenciaDTO(
                    coincidencia_id=coincidencia.id,
                    reporte_perdido_id=r.reporte_perdido_id,
                    reporte_encontrado_id=r.reporte_encontrado_id,
                    score=r.score,
                )
            )

            for reporte_id in (r.reporte_perdido_id, r.reporte_encontrado_id):
                if reporte_id not in ids_actualizados:
                    await publicar_actualizar_estado_reporte(reporte_id, "EN_PROCESO")
                    ids_actualizados.add(reporte_id)


def listar_por_reporte(reporte_id: int, db: Session) -> list[CoincidenciaResponse]:
    rows = (
        db.query(Coincidencia)
        .filter(
            (Coincidencia.reporte_perdido_id == reporte_id)
            | (Coincidencia.reporte_encontrado_id == reporte_id)
        )
        .order_by(Coincidencia.score.desc())
        .all()
    )
    return [CoincidenciaResponse.model_validate(r) for r in rows]


def actualizar_estado(
    coincidencia_id: int, dto: ActualizarEstadoDTO, db: Session
) -> CoincidenciaResponse | None:
    coincidencia = db.get(Coincidencia, coincidencia_id)
    if not coincidencia:
        return None
    coincidencia.estado = dto.estado
    if dto.estado == EstadoCoincidencia.CONFIRMADA:
        from datetime import datetime
        coincidencia.fecha_confirmacion = datetime.utcnow()
    db.commit()
    db.refresh(coincidencia)
    return CoincidenciaResponse.model_validate(coincidencia)
