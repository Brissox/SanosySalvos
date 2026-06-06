package petly.sanosysalvos.cl.reportes;

import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import petly.sanosysalvos.cl.reportes.DTO.ReporteEventoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteVencimientoDTO;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEventoPublisher;

public class ReporteEventoPublisherAdditionalTest {

    @Test
    void publicar_callsRabbit() {
        RabbitTemplate rabbit = Mockito.mock(RabbitTemplate.class);
        ReporteEventoPublisher publisher = new ReporteEventoPublisher(rabbit);

        ReflectionTestUtils.setField(publisher, "queueReporteNuevo", "qNuevo");
        ReflectionTestUtils.setField(publisher, "queueReporteCerrado", "qCerrado");
        ReflectionTestUtils.setField(publisher, "queueReporteProximoVencer", "qProx");

        ReporteEventoDTO evento = ReporteEventoDTO.builder().reporteId(1L).build();
        publisher.publicarReporteNuevo(evento);
        verify(rabbit).convertAndSend("qNuevo", evento);

        publisher.publicarReporteCerrado(2L);
        verify(rabbit).convertAndSend("qCerrado", Map.of("reporteId", 2L));

        ReporteVencimientoDTO venc = new ReporteVencimientoDTO(3L, 123, "PERDIDO", LocalDateTime.now());
        publisher.publicarReporteProximoVencer(venc);
        verify(rabbit).convertAndSend("qProx", venc);
    }
}