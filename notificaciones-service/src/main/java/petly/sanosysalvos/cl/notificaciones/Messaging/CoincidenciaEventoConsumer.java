package petly.sanosysalvos.cl.notificaciones.Messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import petly.sanosysalvos.cl.notificaciones.DTO.CoincidenciaEventoDTO;
import petly.sanosysalvos.cl.notificaciones.Services.CoincidenciaService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CoincidenciaEventoConsumer {

    private final CoincidenciaService coincidenciaService;

    @RabbitListener(queues = "${rabbitmq.queue.notificacion-coincidencia}")
    public void procesarCoincidencia(CoincidenciaEventoDTO evento) {
        log.info("Evento recibido de coincidencias-service: id={}, score={}",
            evento.getCoincidenciaId(), evento.getScore());
        try {
            coincidenciaService.procesarEvento(evento);
        } catch (Exception e) {
            log.error("Error procesando evento coincidencia_id={}: {}",
                evento.getCoincidenciaId(), e.getMessage());
        }
    }
}
