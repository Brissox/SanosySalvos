import pytest

from cache import redis_client
from messaging import publisher
from schemas.schemas import NotificacionCoincidenciaDTO


def test_get_client_sin_conectar_lanza_error(monkeypatch):
    monkeypatch.setattr(redis_client, "_client", None)

    with pytest.raises(RuntimeError, match="Redis no inicializado"):
        redis_client.get_client()


@pytest.mark.asyncio
async def test_publicar_notificacion_sin_channel_no_falla(monkeypatch):
    monkeypatch.setattr(publisher, "_channel", None)

    dto = NotificacionCoincidenciaDTO(
        coincidencia_id=1,
        reporte_perdido_id=10,
        reporte_encontrado_id=20,
        score=0.9,
    )

    await publisher.publicar_notificacion(dto)


@pytest.mark.asyncio
async def test_publicar_actualizar_estado_sin_channel_no_falla(monkeypatch):
    monkeypatch.setattr(publisher, "_channel", None)

    await publisher.publicar_actualizar_estado_reporte(10, "ACTIVO")