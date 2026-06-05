package petly.sanosysalvos.cl.notificaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import petly.sanosysalvos.cl.notificaciones.Controller.CoincidenciaController;
import petly.sanosysalvos.cl.notificaciones.Model.Coincidencia;
import petly.sanosysalvos.cl.notificaciones.Services.CoincidenciaService;

@ExtendWith(MockitoExtension.class)
class CoincidenciaControllerTest {

    @Mock
    private CoincidenciaService coincidenciaService;

    @InjectMocks
    private CoincidenciaController controller;

    @Test
    void buscarPorUsuario() {
        Coincidencia coincidencia = new Coincidencia();
        coincidencia.setId(1L);

        when(coincidenciaService.buscarPorUsuario(12345678)).thenReturn(List.of(coincidencia));

        ResponseEntity<List<Coincidencia>> response = controller.buscarPorUsuario(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}