package petly.sanosysalvos.cl.reportes.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.reportes.Model.Reporte;

public interface  ReporteRepository extends JpaRepository<Reporte, Long> {
    
}
