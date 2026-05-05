package petly.sanosysalvos.cl.reportes.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;

public interface  ReporteRepository extends JpaRepository<Reporte, Long> {
    
    List<Reporte> findByTipoReporte(TipoReporte tipoReporte);

    List<Reporte> findByEstadoReporte(EstadoReporte estadoReporte);
}