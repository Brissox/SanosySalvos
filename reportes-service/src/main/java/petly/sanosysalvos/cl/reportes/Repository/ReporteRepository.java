package petly.sanosysalvos.cl.reportes.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;

public interface  ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipoReporte(TipoReporte tipoReporte);

    List<Reporte> findByEstadoReporte(EstadoReporte estadoReporte);

    List<Reporte> findByRunUsuario(Integer runUsuario);

    @Query("SELECT r FROM Reporte r WHERE r.fechaLimite <= :ahora AND r.estadoReporte IN :estados")
    List<Reporte> findVencidos(@Param("ahora") LocalDateTime ahora, @Param("estados") List<EstadoReporte> estados);

    @Query("SELECT r FROM Reporte r WHERE r.fechaLimite BETWEEN :desde AND :hasta AND r.estadoReporte IN :estados")
    List<Reporte> findProximosAVencer(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("estados") List<EstadoReporte> estados);
}
