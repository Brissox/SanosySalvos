package petly.sanosysalvos.cl.notificaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import petly.sanosysalvos.cl.notificaciones.Config.JwtUtil;
import petly.sanosysalvos.cl.notificaciones.Controller.NotificacionController;
import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;

@ExtendWith(MockitoExtension.class)
class NotificacionesControllerTest {

    @Mock
    private NotificacionServices notificacionServices;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private NotificacionController controller;

    @Test
    void listarPorUsuario_tokenValido() {
        when(jwtUtil.extractUserId("token")).thenReturn(123L);
        when(notificacionServices.buscarPorUsuario(123L)).thenReturn(List.of(new Notificacion()));

        ResponseEntity<?> response = controller.listarPorUsuario("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);
    }

    @Test
    void listarPorUsuario_tokenInvalido() {
        when(jwtUtil.extractUserId("token")).thenThrow(new RuntimeException("token malo"));

        ResponseEntity<?> response = controller.listarPorUsuario("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void buscarPorId_existente() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);

        when(notificacionServices.buscarPorId(1L)).thenReturn(notificacion);

        ResponseEntity<?> response = controller.buscarPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Notificacion) response.getBody()).getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_noExistente() {
        when(notificacionServices.buscarPorId(1L))
                .thenThrow(new EntityNotFoundException("No encontrada"));

        ResponseEntity<?> response = controller.buscarPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void buscarNoLeidas_tokenValido() {
        when(jwtUtil.extractUserId("token")).thenReturn(123L);
        when(notificacionServices.buscarNoLeidas(123L)).thenReturn(List.of(new Notificacion()));

        ResponseEntity<?> response = controller.buscarNoLeidas("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void contarNoLeidas_tokenValido() {
        when(jwtUtil.extractUserId("token")).thenReturn(123L);
        when(notificacionServices.contarNoLeidas(123L)).thenReturn(4L);

        ResponseEntity<?> response = controller.contarNoLeidas("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
         Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("noLeidas")).isEqualTo(4L);
    }

    @Test
    void marcarComoLeida_existente() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setLeida(true);

        when(notificacionServices.marcarComoLeida(1L)).thenReturn(notificacion);

        ResponseEntity<?> response = controller.marcarComoLeida(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Notificacion) response.getBody()).isLeida()).isTrue();
    }

    @Test
    void marcarTodasComoLeidas_tokenValido() {
        when(jwtUtil.extractUserId("token")).thenReturn(123L);
        when(notificacionServices.marcarTodasComoLeidas(123L)).thenReturn(3);

        ResponseEntity<?> response = controller.marcarTodasComoLeidas("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("actualizadas")).isEqualTo(3);
    }

    @Test
    void eliminar_existente() {
        ResponseEntity<?> response = controller.eliminar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(notificacionServices).eliminar(1L);
    }

    @Test
    void eliminar_noExistente() {
        doThrow(new EntityNotFoundException("No encontrada"))
                .when(notificacionServices).eliminar(1L);

        ResponseEntity<?> response = controller.eliminar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}