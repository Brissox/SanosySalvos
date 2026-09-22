package petly.sanosysalvos.cl.notificaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;
import petly.sanosysalvos.cl.notificaciones.Model.TipoNotificacion;
import petly.sanosysalvos.cl.notificaciones.Repository.NotificacionRepository;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;

@ExtendWith(MockitoExtension.class)
class NotificacionServicesTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionServices service;

    @Test
    void buscarPorId_existente_devuelveNotificacion() {
        Notificacion notificacion = crearNotificacion();
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        Notificacion resultado = service.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_noExiste_lanzaEntityNotFoundException() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Notificacion no encontrada");
    }

    @Test
    void buscarPorUsuario_devuelveLista() {
        Notificacion notificacion = crearNotificacion();

        when(notificacionRepository.findByIdUsuarioOrderByFechaCreacionDesc(123L))
                .thenReturn(List.of(notificacion));

        List<Notificacion> resultado = service.buscarPorUsuario(123L);

        assertThat(resultado).hasSize(1);
        verify(notificacionRepository).findByIdUsuarioOrderByFechaCreacionDesc(123L);
    }

    @Test
    void buscarNoLeidas_devuelveListaNoLeida() {
        Notificacion notificacion = crearNotificacion();
        notificacion.setLeida(false);

        when(notificacionRepository.findByIdUsuarioAndLeidaOrderByFechaCreacionDesc(123L, false))
                .thenReturn(List.of(notificacion));

        List<Notificacion> resultado = service.buscarNoLeidas(123L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).isLeida()).isFalse();
    }

    @Test
    void contarNoLeidas_devuelveCantidad() {
        when(notificacionRepository.countByIdUsuarioAndLeida(123L, false)).thenReturn(3L);

        long resultado = service.contarNoLeidas(123L);

        assertThat(resultado).isEqualTo(3L);
    }

    @Test
    void marcarComoLeida_actualizaLeidaTrue() {
        Notificacion notificacion = crearNotificacion();
        notificacion.setLeida(false);

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(notificacion)).thenReturn(notificacion);

        Notificacion resultado = service.marcarComoLeida(1L);

        assertThat(resultado.isLeida()).isTrue();
        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void marcarTodasComoLeidas_devuelveCantidadActualizada() {
        when(notificacionRepository.marcarTodasComoLeidas(123L)).thenReturn(5);

        int resultado = service.marcarTodasComoLeidas(123L);

        assertThat(resultado).isEqualTo(5);
    }

    @Test
    void eliminar_existente_eliminaPorId() {
        when(notificacionRepository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(notificacionRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExiste_lanzaEntityNotFoundException() {
        when(notificacionRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void crearNotificacionVencimiento_guardaNotificacion() {
        when(notificacionRepository.save(any(Notificacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notificacion resultado = service.crearNotificacionVencimiento(
                123L,
                10L,
                "PERDIDA",
                "30/05/2026");

        assertThat(resultado.getTitulo()).isEqualTo("Tu reporte está por vencer");
        assertThat(resultado.getTipo()).isEqualTo(TipoNotificacion.REPORTE_PROXIMO_VENCER);
        assertThat(resultado.getIdUsuario()).isEqualTo(123L);
        assertThat(resultado.getIdReporte()).isEqualTo(10L);
        assertThat(resultado.isLeida()).isFalse();
    }

    @Test
    void crearNotificacionCoincidencia_guardaNotificacion() {
        when(notificacionRepository.save(any(Notificacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notificacion resultado = service.crearNotificacionCoincidencia(
                123L,
                10L,
                20L,
                99L,
                "Titulo",
                "Mensaje");

        assertThat(resultado.getTitulo()).isEqualTo("Titulo");
        assertThat(resultado.getMensaje()).isEqualTo("Mensaje");
        assertThat(resultado.getTipo()).isEqualTo(TipoNotificacion.COINCIDENCIA_POTENCIAL);
        assertThat(resultado.getIdCoincidencia()).isEqualTo(99L);
        assertThat(resultado.getIdReporteCoincidencia()).isEqualTo(20L);
    }

    private Notificacion crearNotificacion() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setTitulo("Titulo");
        notificacion.setMensaje("Mensaje");
        notificacion.setIdUsuario(123L);
        notificacion.setLeida(false);
        notificacion.setTipo(TipoNotificacion.SISTEMA);
        return notificacion;
    }
}