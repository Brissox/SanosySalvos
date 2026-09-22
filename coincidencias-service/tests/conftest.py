from datetime import datetime
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from db.database import Base
from db.models import Especie, EstadoReporte, TipoReporte
from schemas.schemas import ReporteEventoDTO


@pytest.fixture
def db_session():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)
    Session = sessionmaker(bind=engine)
    db = Session()
    try:
        yield db
    finally:
        db.close()


def reporte(
    reporte_id=1,
    tipo=TipoReporte.PERDIDA,
    especie=Especie.PERRO,
    estado=EstadoReporte.ACTIVO,
    lat=-33.45,
    lon=-70.66,
    fecha=None,
    raza="poodle",
    color="blanco",
    descripcion="perro blanco pequeno",
):
    return ReporteEventoDTO(
        reporte_id=reporte_id,
        tipo_reporte=tipo,
        especie=especie,
        raza=raza,
        color_principal=color,
        latitud=lat,
        longitud=lon,
        fecha_reporte=fecha or datetime(2026, 1, 1),
        descripcion=descripcion,
        estado_reporte=estado,
    )