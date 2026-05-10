"""
Combina los features individuales en un score final ponderado.
Los pesos provienen de config.settings para que sean ajustables sin tocar código.
"""

from config import settings
from matching.embeddings import similitud_coseno_lote
from matching.features import (
    score_especie,
    score_temporal,
    score_ubicacion,
)
from schemas.schemas import ReporteEventoDTO, ScoreDesglose

_AUSENTE = 0.5


def _scores_embedding(
    nuevo: ReporteEventoDTO, candidato: ReporteEventoDTO
) -> tuple[float, float, float]:
    """Calcula descripcion, raza y color en un único model.encode()."""
    campos = [
        (nuevo.descripcion, candidato.descripcion),
        (nuevo.raza, candidato.raza),
        (nuevo.color_principal, candidato.color_principal),
    ]
    idx_map: dict[int, int] = {}
    pares: list[tuple[str, str]] = []
    for i, (a_val, b_val) in enumerate(campos):
        a_text = (a_val or "").strip()
        b_text = (b_val or "").strip()
        if a_text and b_text:
            idx_map[i] = len(pares)
            pares.append((a_text, b_text))

    sims = similitud_coseno_lote(pares) if pares else []
    return tuple(sims[idx_map[i]] if i in idx_map else _AUSENTE for i in range(3))


def calcular_score(nuevo: ReporteEventoDTO, candidato: ReporteEventoDTO) -> ScoreDesglose:
    s_especie = score_especie(nuevo, candidato)
    s_ubicacion = score_ubicacion(nuevo, candidato, settings.km_max_distancia)
    s_temporal = score_temporal(nuevo, candidato, settings.dias_max_diferencia)
    s_descripcion, s_raza, s_color = _scores_embedding(nuevo, candidato)

    total = (
        s_especie * settings.peso_especie
        + s_raza * settings.peso_raza
        + s_color * settings.peso_color
        + s_ubicacion * settings.peso_ubicacion
        + s_temporal * settings.peso_temporal
        + s_descripcion * settings.peso_descripcion
    )

    return ScoreDesglose(
        especie=round(s_especie, 4),
        raza=round(s_raza, 4),
        color=round(s_color, 4),
        ubicacion=round(s_ubicacion, 4),
        temporal=round(s_temporal, 4),
        descripcion=round(s_descripcion, 4),
        total=round(total, 4),
    )
