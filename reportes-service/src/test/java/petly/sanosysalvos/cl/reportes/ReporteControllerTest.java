package petly.sanosysalvos.cl.reportes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import petly.sanosysalvos.cl.reportes.Config.JwtUtil;
import petly.sanosysalvos.cl.reportes.Controller.ReporteController;
import petly.sanosysalvos.cl.reportes.DTO.ReporteGeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteRequest;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;
import petly.sanosysalvos.cl.reportes.Services.ReporteServices;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteServices reporteServices;

    @Mock
    private JwtUtil jwtUtil;

    private ReporteController controller;

    @BeforeEach
    void setUp() {
        controller = new ReporteController(reporteServices);
        ReflectionTestUtils.setField(controller, "jwtUtil", jwtUtil);
    }

    @Test
    void listar_sinReportes() {
        when(reporteServices.listar()).thenReturn(List.of());

        ResponseEntity<List<ReporteGeoDTO>> response = controller.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(reporteServices).listar();
    }

    @Test
    void buscarTodos_reportes() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(1L);

        when(reporteServices.buscarTodos()).thenReturn(List.of(reporte));

        ResponseEntity<List<Reporte>> response = controller.buscarTodos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getIdreporte()).isEqualTo(1L);
        verify(reporteServices).buscarTodos();
    }

    @Test
    void buscarPorId_reporte() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(1L);

        when(reporteServices.buscarPorId(1L)).thenReturn(reporte);

        ResponseEntity<Reporte> response = controller.buscarPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getIdreporte()).isEqualTo(1L);
        verify(reporteServices).buscarPorId(1L);
    }

    @Test
    void crearReporte_conImagen() throws Exception {
        ReporteRequest request = new ReporteRequest();
        MultipartFile imagen = new MockMultipartFile(
                "imagen",
                "imagen.jpg",
                "image/jpeg",
                "fake-image".getBytes());

        Reporte reporte = new Reporte();
        reporte.setIdreporte(99L);

        when(jwtUtil.extractRun("token")).thenReturn(777);
        when(reporteServices.crear(any(ReporteRequest.class), eq(imagen), eq(777)))
                .thenReturn(reporte);

        ResponseEntity<Reporte> response = controller.crear(
                "Bearer token",
                request,
                imagen);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getIdreporte()).isEqualTo(99L);
        verify(jwtUtil).extractRun("token");
        verify(reporteServices).crear(request, imagen, 777);
    }

    @Test
    void crearReporte_sinImagen() throws Exception {
        ReporteRequest request = new ReporteRequest();

        Reporte reporte = new Reporte();
        reporte.setIdreporte(100L);

        when(jwtUtil.extractRun("token")).thenReturn(777);
        when(reporteServices.crear(any(ReporteRequest.class), isNull(), eq(777)))
                .thenReturn(reporte);

        ResponseEntity<Reporte> response = controller.crear(
                "Bearer token",
                request,
                null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getIdreporte()).isEqualTo(100L);
        verify(jwtUtil).extractRun("token");
        verify(reporteServices).crear(request, null, 777);
    }

    @Test
    void crear_JwtFalla() {
        ReporteRequest request = new ReporteRequest();

        when(jwtUtil.extractRun("token"))
                .thenThrow(new RuntimeException("Token invalido"));

        assertThatThrownBy(() -> controller.crear("Bearer token", request, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error creando reporte");
    }

    @Test
    void eliminar_reporte() {
        ResponseEntity<Void> response = controller.eliminar(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(reporteServices).eliminarGeo(5L);
    }

    @Test
    void eliminar_servicioFalla() {
        org.mockito.Mockito.doThrow(new RuntimeException("No existe"))
                .when(reporteServices)
                .eliminarGeo(5L);

        assertThatThrownBy(() -> controller.eliminar(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error eliminando reporte");
    }

    @Test
    void filtrarPorTipo_reportes() {
        when(reporteServices.filtrarPorTipo(TipoReporte.PERDIDA))
                .thenReturn(List.of());

        ResponseEntity<List<ReporteGeoDTO>> response =
                controller.filtrarPorTipo(TipoReporte.PERDIDA);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(reporteServices).filtrarPorTipo(TipoReporte.PERDIDA);
    }

    @Test
    void filtrarPorEstado_reportes() {
        when(reporteServices.filtrarPorEstado(EstadoReporte.ACTIVO))
                .thenReturn(List.of());

        ResponseEntity<List<ReporteGeoDTO>> response =
                controller.filtrarPorEstado(EstadoReporte.ACTIVO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(reporteServices).filtrarPorEstado(EstadoReporte.ACTIVO);
    }

    @Test
    void buscarPorRunUsuario() {
        when(reporteServices.buscarPorRunUsuario(12345678))
                .thenReturn(List.of());

        ResponseEntity<List<ReporteGeoDTO>> response =
                controller.buscarPorRunUsuario(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(reporteServices).buscarPorRunUsuario(12345678);
    }

    @Test
    void renovar_reporte() {
        ResponseEntity<Void> response = controller.renovar(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(reporteServices).renovarReporte(10L);
    }
}