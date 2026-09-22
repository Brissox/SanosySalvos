package petly.sanosysalvos.cl.reportes;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import petly.sanosysalvos.cl.reportes.DTO.ActualizarEstadoReporteDTO;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEstadoConsumer;
import petly.sanosysalvos.cl.reportes.Services.ReporteServices;

@ExtendWith(MockitoExtension.class)
class ReporteEstadoConsumerTest {

    @Mock
    private ReporteServices reporteServices;

    @InjectMocks
    private ReporteEstadoConsumer consumer;

    @Test
    void onActualizarEstado() {
        ActualizarEstadoReporteDTO dto = new ActualizarEstadoReporteDTO();
        dto.setReporteId(1L);
        dto.setNuevoEstado("RESUELTO");

        consumer.onActualizarEstado(dto);

        verify(reporteServices).actualizarEstado(1L, EstadoReporte.RESUELTO);
    }
}