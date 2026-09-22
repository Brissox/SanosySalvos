package petly.sanosysalvos.cl.reportes;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import petly.sanosysalvos.cl.reportes.DTO.ReporteEventoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteVencimientoDTO;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEventoPublisher;

@ExtendWith(MockitoExtension.class)
class ReporteEventoPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReporteEventoPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "queueReporteNuevo", "queue.reporte.nuevo");
        ReflectionTestUtils.setField(publisher, "queueReporteCerrado", "queue.reporte.cerrado");
        ReflectionTestUtils.setField(publisher, "queueReporteProximoVencer", "queue.reporte.proximo.vencer");
    }

    @Test
    void publicarReporteNuevo() {
        ReporteEventoDTO dto = ReporteEventoDTO.builder().reporteId(1L).build();
        publisher.publicarReporteNuevo(dto);
        verify(rabbitTemplate).convertAndSend(eq("queue.reporte.nuevo"), eq(dto));
    }

    @Test
    void publicarReporteCerrado() {
        publisher.publicarReporteCerrado(2L);
        verify(rabbitTemplate).convertAndSend(eq("queue.reporte.cerrado"), eq(Map.of("reporteId", 2L)));
    }

    @Test
    void publicarReporte_proximoVencer() {
        ReporteVencimientoDTO dto = new ReporteVencimientoDTO();
        publisher.publicarReporteProximoVencer(dto);
        verify(rabbitTemplate).convertAndSend(eq("queue.reporte.proximo.vencer"), eq(dto));
    }
}