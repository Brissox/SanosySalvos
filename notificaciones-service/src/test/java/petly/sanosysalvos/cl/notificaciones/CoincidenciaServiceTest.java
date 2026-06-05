package petly.sanosysalvos.cl.notificaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import petly.sanosysalvos.cl.notificaciones.DTO.CoincidenciaEventoDTO;
import petly.sanosysalvos.cl.notificaciones.DTO.ReporteDetalleDTO;
import petly.sanosysalvos.cl.notificaciones.Model.Coincidencia;
import petly.sanosysalvos.cl.notificaciones.Repository.CoincidenciaRepository;
import petly.sanosysalvos.cl.notificaciones.Services.CoincidenciaService;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;

@ExtendWith(MockitoExtension.class)
class CoincidenciaServiceTest {

    @Mock
    private CoincidenciaRepository coincidenciaRepository;

    @Mock
    private NotificacionServices notificacionServices;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CoincidenciaService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "reportesUrl", "http://reportes-service");
    }

    @Test
    void procesarEvento_usuariosDistintos() {
        CoincidenciaEventoDTO evento = crearEvento();

        ReporteDetalleDTO perdido = new ReporteDetalleDTO();
        perdido.setRunUsuario(111);

        ReporteDetalleDTO encontrado = new ReporteDetalleDTO();
        encontrado.setRunUsuario(222);

        Coincidencia guardada = Coincidencia.builder()
                .id(99L)
                .coincidenciaIdRef(100L)
                .reportePerdidoId(10L)
                .reporteEncontradoId(20L)
                .score(0.87)
                .runUsuarioPerdido(111)
                .runUsuarioEncontrado(222)
                .build();

        when(coincidenciaRepository.findByCoincidenciaIdRef(100L)).thenReturn(Optional.empty());
        when(restTemplate.getForObject("http://reportes-service/petly/reportes/10", ReporteDetalleDTO.class))
                .thenReturn(perdido);
        when(restTemplate.getForObject("http://reportes-service/petly/reportes/20", ReporteDetalleDTO.class))
                .thenReturn(encontrado);
        when(coincidenciaRepository.save(any(Coincidencia.class))).thenReturn(guardada);

        service.procesarEvento(evento);

        verify(coincidenciaRepository).save(any(Coincidencia.class));
        verify(notificacionServices, times(2)).crearNotificacionCoincidencia(
                anyLong(), anyLong(), anyLong(), eq(99L), anyString(), anyString());
    }

    @Test
    void procesarEvento_duplicado() {
        CoincidenciaEventoDTO evento = crearEvento();

        when(coincidenciaRepository.findByCoincidenciaIdRef(100L))
                .thenReturn(Optional.of(new Coincidencia()));

        service.procesarEvento(evento);

        verify(coincidenciaRepository, never()).save(any());
        verifyNoInteractions(notificacionServices);
    }

    @Test
    void buscarPorUsuario_perdidoYEncontrado() {
        Coincidencia c1 = Coincidencia.builder().id(1L).runUsuarioPerdido(123).build();
        Coincidencia c2 = Coincidencia.builder().id(2L).runUsuarioEncontrado(123).build();

        when(coincidenciaRepository.findByRunUsuarioPerdido(123)).thenReturn(List.of(c1));
        when(coincidenciaRepository.findByRunUsuarioEncontrado(123)).thenReturn(List.of(c2));

        List<Coincidencia> resultado = service.buscarPorUsuario(123);

        assertThat(resultado).hasSize(2);
    }

    private CoincidenciaEventoDTO crearEvento() {
        CoincidenciaEventoDTO evento = new CoincidenciaEventoDTO();
        evento.setCoincidenciaId(100L);
        evento.setReportePerdidoId(10L);
        evento.setReporteEncontradoId(20L);
        evento.setScore(0.87);
        return evento;
    }
}