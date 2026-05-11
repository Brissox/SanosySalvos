package petly.sanosysalvos.cl.reportes.Scheduling;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.reportes.DTO.ReporteVencimientoDTO;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEventoPublisher;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;

@Component
public class ReporteExpirationScheduler {

    private final ReporteRepository reporteRepository;
    private final ReporteEventoPublisher publisher;

    public ReporteExpirationScheduler(ReporteRepository reporteRepository, ReporteEventoPublisher publisher) {
        this.reporteRepository = reporteRepository;
        this.publisher = publisher;
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void procesarVencimientos() {
        LocalDateTime ahora = LocalDateTime.now();
        List<EstadoReporte> estadosActivos = List.of(EstadoReporte.ACTIVO, EstadoReporte.EN_PROCESO);

        List<Reporte> vencidos = reporteRepository.findVencidos(ahora, estadosActivos);
        for (Reporte r : vencidos) {
            r.setEstadoReporte(EstadoReporte.CERRADO);
            reporteRepository.save(r);
            publisher.publicarReporteCerrado(r.getIdreporte());
        }

        // Notificar reportes que vencen en los próximos 3 días
        LocalDateTime en3Dias = ahora.plusDays(3);
        List<Reporte> proximos = reporteRepository.findProximosAVencer(ahora, en3Dias, estadosActivos);
        for (Reporte r : proximos) {
            ReporteVencimientoDTO dto = ReporteVencimientoDTO.builder()
                    .reporteId(r.getIdreporte())
                    .runUsuario(r.getRunUsuario())
                    .tipoReporte(r.getTipoReporte().name())
                    .fechaLimite(r.getFechaLimite())
                    .build();
            publisher.publicarReporteProximoVencer(dto);
        }
    }
}
