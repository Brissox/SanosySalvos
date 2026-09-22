import pytest

from db.models import Coincidencia, EstadoCoincidencia, EstadoReporte, TipoReporte
from matching.engine import ejecutar_matching
from schemas.schemas import ScoreDesglose, ActualizarEstadoDTO
from services.coincidencia_service import actualizar_estado, listar_por_reporte, registrar_reporte_en_cache
from tests.conftest import reporte


def test_ejecutar_matching_guarda_y_ordena(db_session, monkeypatch):
    nuevo = reporte(reporte_id=1, tipo=TipoReporte.PERDIDA)
    candidato = reporte(reporte_id=2, tipo=TipoReporte.ENCONTRADA)

    monkeypatch.setattr("matching.engine.settings.score_threshold", 0.55)
    monkeypatch.setattr(
        "matching.engine.calcular_score",
        lambda a, b: ScoreDesglose(
            especie=1, raza=1, color=1, ubicacion=1, temporal=1, descripcion=1, total=0.9
        ),
    )

    resultados = ejecutar_matching(nuevo, [candidato], db_session)

    assert len(resultados) == 1
    assert resultados[0].reporte_perdido_id == 1
    assert resultados[0].reporte_encontrado_id == 2
    assert db_session.query(Coincidencia).count() == 1


def test_ejecutar_matching_ignora_mismo_tipo(db_session, monkeypatch):
    nuevo = reporte(reporte_id=1, tipo=TipoReporte.PERDIDA)
    candidato = reporte(reporte_id=2, tipo=TipoReporte.PERDIDA)

    resultados = ejecutar_matching(nuevo, [candidato], db_session)

    assert resultados == []
    assert db_session.query(Coincidencia).count() == 0


def test_listar_y_actualizar_estado(db_session):
    c = Coincidencia(
        reporte_perdido_id=1,
        reporte_encontrado_id=2,
        score=0.8,
        score_especie=1,
        score_raza=1,
        score_color=1,
        score_ubicacion=1,
        score_temporal=1,
        score_descripcion=1,
        estado=EstadoCoincidencia.PENDIENTE,
    )
    db_session.add(c)
    db_session.commit()

    encontrados = listar_por_reporte(1, db_session)
    actualizado = actualizar_estado(
        c.id,
        ActualizarEstadoDTO(estado=EstadoCoincidencia.CONFIRMADA),
        db_session,
    )

    assert len(encontrados) == 1
    assert actualizado.estado == EstadoCoincidencia.CONFIRMADA
    assert actualizado.fecha_confirmacion is not None


@pytest.mark.asyncio
async def test_registrar_reporte_resuelto_no_usa_redis(monkeypatch):
    llamado = False

    def fake_get_client():
        nonlocal llamado
        llamado = True

    monkeypatch.setattr("services.coincidencia_service.get_client", fake_get_client)

    await registrar_reporte_en_cache(
        reporte(reporte_id=10, estado=EstadoReporte.RESUELTO)
    )

    assert llamado is False


def test_actualizar_estado_retorna_none_si_no_existe(db_session):
    dto = ActualizarEstadoDTO(estado=EstadoCoincidencia.DESCARTADA)

    resultado = actualizar_estado(999, dto, db_session)

    assert resultado is None


def test_ejecutar_matching_filtra_score_bajo(db_session, monkeypatch):
    nuevo = reporte(reporte_id=1, tipo=TipoReporte.PERDIDA)
    candidato = reporte(reporte_id=2, tipo=TipoReporte.ENCONTRADA)

    monkeypatch.setattr("matching.engine.settings.score_threshold", 0.55)
    monkeypatch.setattr(
        "matching.engine.calcular_score",
        lambda a, b: ScoreDesglose(
            especie=1, raza=1, color=1, ubicacion=1, temporal=1, descripcion=1, total=0.2
        ),
    )

    resultados = ejecutar_matching(nuevo, [candidato], db_session)

    assert resultados == []
    assert db_session.query(Coincidencia).count() == 0


def test_ejecutar_matching_con_nuevo_encontrado_invierte_ids(db_session, monkeypatch):
    nuevo = reporte(reporte_id=2, tipo=TipoReporte.ENCONTRADA)
    candidato = reporte(reporte_id=1, tipo=TipoReporte.PERDIDA)

    monkeypatch.setattr("matching.engine.settings.score_threshold", 0.55)
    monkeypatch.setattr(
        "matching.engine.calcular_score",
        lambda a, b: ScoreDesglose(
            especie=1, raza=1, color=1, ubicacion=1, temporal=1, descripcion=1, total=0.9
        ),
    )

    resultados = ejecutar_matching(nuevo, [candidato], db_session)

    assert resultados[0].reporte_perdido_id == 1
    assert resultados[0].reporte_encontrado_id == 2