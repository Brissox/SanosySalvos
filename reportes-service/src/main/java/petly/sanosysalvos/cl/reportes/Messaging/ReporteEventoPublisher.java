package petly.sanosysalvos.cl.reportes.Messaging;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import petly.sanosysalvos.cl.reportes.DTO.ReporteEventoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteVencimientoDTO;

@Component
public class ReporteEventoPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.reporte-nuevo}")
    private String queueReporteNuevo;

    @Value("${rabbitmq.queue.reporte-cerrado}")
    private String queueReporteCerrado;

    @Value("${rabbitmq.queue.reporte-proximo-vencer}")
    private String queueReporteProximoVencer;

    public ReporteEventoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarReporteNuevo(ReporteEventoDTO dto) {
        rabbitTemplate.convertAndSend(queueReporteNuevo, dto);
    }

    public void publicarReporteCerrado(Long reporteId) {
        rabbitTemplate.convertAndSend(queueReporteCerrado, Map.of("reporteId", reporteId));
    }

    public void publicarReporteProximoVencer(ReporteVencimientoDTO dto) {
        rabbitTemplate.convertAndSend(queueReporteProximoVencer, dto);
    }
}
