package petly.sanosysalvos.cl.geolocalizacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import petly.sanosysalvos.cl.geolocalizacion.Controller.GeoController;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoDTO;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoRequest;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoResponse;
import petly.sanosysalvos.cl.geolocalizacion.Services.GeoServices;

@ExtendWith(MockitoExtension.class)
class GeoControllerTest {

    @Mock
    private GeoServices service;

    @InjectMocks
    private GeoController controller;

    @Test
    void crear_ubicacion() {
        GeoRequest request = new GeoRequest(-33.45, -70.66);
        when(service.crear(request)).thenReturn(new GeoResponse(1L));

        GeoResponse response = controller.crear(request);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void obtener_geolocalizacion() {
        when(service.obtener(1L)).thenReturn(new GeoDTO(1L, -33.45, -70.66));

        GeoDTO response = controller.obtener(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLatitud()).isEqualTo(-33.45);
    }

    @Test
    void buscarTodos_geolocalizacion() {
        when(service.buscarTodos()).thenReturn(List.of(new GeoDTO(1L, -33.45, -70.66)));

        ResponseEntity<List<GeoDTO>> response = controller.buscarTodos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void eliminar_geolocalizacion() {
        ResponseEntity<Void> response = controller.eliminar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).eliminar(1L);
    }

    @Test
    void eliminarDTO_geoDTO() {
        ResponseEntity<Void> response = controller.eliminarDTO(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).eliminarDTO(1L);
    }
}