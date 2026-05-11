package petly.sanosysalvos.cl.reportes.Messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import petly.sanosysalvos.cl.reportes.DTO.ActualizarEstadoReporteDTO;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Services.ReporteServices;

@Component
public class ReporteEstadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReporteEstadoConsumer.class);

    private final ReporteServices reporteServices;

    public ReporteEstadoConsumer(ReporteServices reporteServices) {
        this.reporteServices = reporteServices;
    }

    @RabbitListener(queues = "${rabbitmq.queue.reporte-estado-actualizar}")
    public void onActualizarEstado(ActualizarEstadoReporteDTO dto) {
        try {
            EstadoReporte nuevoEstado = EstadoReporte.valueOf(dto.getNuevoEstado());
            reporteServices.actualizarEstado(dto.getReporteId(), nuevoEstado);
            log.info("Reporte {} actualizado a estado {}", dto.getReporteId(), nuevoEstado);
        } catch (IllegalArgumentException e) {
            log.error("Estado inválido recibido para reporte {}: {}", dto.getReporteId(), dto.getNuevoEstado());
        } catch (RuntimeException e) {
            log.error("Error actualizando estado del reporte {}: {}", dto.getReporteId(), e.getMessage());
        }
    }
}
