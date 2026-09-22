package petly.sanosysalvos.cl.notificaciones.Messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import petly.sanosysalvos.cl.notificaciones.DTO.ReporteVencimientoDTO;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;

import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReporteVencimientoConsumer {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final NotificacionServices notificacionServices;

    @RabbitListener(queues = "${rabbitmq.queue.reporte-proximo-vencer}")
    public void procesarVencimiento(ReporteVencimientoDTO evento) {
        log.info("Reporte próximo a vencer recibido: id={}, run={}, fechaLimite={}",
            evento.getReporteId(), evento.getRunUsuario(), evento.getFechaLimite());
        try {
            notificacionServices.crearNotificacionVencimiento(
                evento.getRunUsuario().longValue(),
                evento.getReporteId(),
                evento.getTipoReporte(),
                evento.getFechaLimite().format(FORMATTER)
            );
        } catch (Exception e) {
            log.error("Error procesando vencimiento reporte_id={}: {}",
                evento.getReporteId(), e.getMessage());
            throw e;
        }
    }
}
