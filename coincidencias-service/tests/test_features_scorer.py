from datetime import datetime

from db.models import Especie, TipoReporte
from matching.features import score_especie, score_temporal, score_ubicacion
from matching.scorer import calcular_score, _scores_embedding
from tests.conftest import reporte


def test_score_especie_igual_y_distinta():
    a = reporte(especie=Especie.PERRO)
    b = reporte(reporte_id=2, especie=Especie.PERRO)
    c = reporte(reporte_id=3, especie=Especie.GATO)

    assert score_especie(a, b) == 1.0
    assert score_especie(a, c) == 0.0


def test_score_ubicacion_sin_coordenadas_retorna_incertidumbre():
    a = reporte(lat=None)
    b = reporte(reporte_id=2)

    assert score_ubicacion(a, b, 50) == 0.5


def test_score_temporal_mismo_dia_y_fuera_de_rango():
    a = reporte(fecha=datetime(2026, 1, 1))
    b = reporte(reporte_id=2, fecha=datetime(2026, 1, 1))
    c = reporte(reporte_id=3, fecha=datetime(2026, 4, 1))

    assert score_temporal(a, b, 60) == 1.0
    assert score_temporal(a, c, 60) == 0.0


def test_scores_embedding_usa_ausente_y_mock(monkeypatch):
    a = reporte(descripcion="perro blanco", raza=None, color="blanco")
    b = reporte(reporte_id=2, descripcion="perro claro", raza="poodle", color="claro")

    monkeypatch.setattr("matching.scorer.similitud_coseno_lote", lambda pares: [0.9, 0.7])

    assert _scores_embedding(a, b) == (0.9, 0.5, 0.7)


def test_calcular_score_pondera_componentes(monkeypatch):
    a = reporte(tipo=TipoReporte.PERDIDA)
    b = reporte(reporte_id=2, tipo=TipoReporte.ENCONTRADA)

    monkeypatch.setattr("matching.scorer.similitud_coseno_lote", lambda pares: [1.0, 1.0, 1.0])

    score = calcular_score(a, b)

    assert score.especie == 1.0
    assert score.raza == 1.0
    assert score.color == 1.0
    assert score.total > 0.8

def test_score_ubicacion_fuera_de_rango():
    a = reporte(lat=-33.45, lon=-70.66)
    b = reporte(reporte_id=2, lat=-34.60, lon=-58.38)

    assert score_ubicacion(a, b, 50) == 0.0


def test_score_raza_color_descripcion_con_mock(monkeypatch):
    from matching.features import score_raza, score_color, score_descripcion

    a = reporte(raza="poodle", color="blanco", descripcion="perro pequeno")
    b = reporte(reporte_id=2, raza="poodle toy", color="claro", descripcion="perro chico")

    monkeypatch.setattr("matching.embeddings.similitud_coseno", lambda x, y: 0.8)

    assert score_raza(a, b) == 0.8
    assert score_color(a, b) == 0.8
    assert score_descripcion(a, b) == 0.8