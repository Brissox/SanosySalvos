package petly.sanosysalvos.cl.mascotas;

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

import petly.sanosysalvos.cl.mascotas.Config.JwtUtil;
import petly.sanosysalvos.cl.mascotas.Controller.MascotaController;
import petly.sanosysalvos.cl.mascotas.DTO.CrearMascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Services.MascotaServices;

@ExtendWith(MockitoExtension.class)
class MascotaControllerTest {

    @Mock
    private MascotaServices mascotaServices;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MascotaController mascotaController;

    @Test
    void listarMascotas_conMascotas() {
        Mascota mascota = new Mascota();
        mascota.setChip("ABC123");

        when(mascotaServices.buscarTodoMascotas()).thenReturn(List.of(mascota));

        ResponseEntity<?> response = mascotaController.ListarMascotas();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);
    }

    @Test
    void listarMascotas_sinMascotas() {
        when(mascotaServices.buscarTodoMascotas()).thenReturn(List.of());

        ResponseEntity<?> response = mascotaController.ListarMascotas();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se encuentran mascotas");
    }

    @Test
    void buscarUnaMascota_conMascota() {
        Mascota mascota = new Mascota();
        mascota.setChip("ABC123");

        when(mascotaServices.buscarUnaMascota("ABC123")).thenReturn(mascota);

        ResponseEntity<?> response = mascotaController.BuscarUnaMascotaPorId("ABC123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Mascota) response.getBody()).getChip()).isEqualTo("ABC123");
    }

    @Test
    void buscarUnaMascota_sinMascota() {
        when(mascotaServices.buscarUnaMascota("NOEXISTE"))
                .thenThrow(new RuntimeException("No encontrada"));

        ResponseEntity<?> response = mascotaController.BuscarUnaMascotaPorId("NOEXISTE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se encuentra la mascota");
    }

    @Test
    void obtenerMisMascotas_tokenValido() {
        Mascota mascota = new Mascota();
        mascota.setChip("ABC123");
        mascota.setRunUsuario(12345678);

        when(jwtUtil.extractRun("token")).thenReturn(12345678);
        when(mascotaServices.buscarMascotasPorRun(12345678)).thenReturn(List.of(mascota));

        ResponseEntity<?> response = mascotaController.obtenerMisMascotas("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);

        verify(jwtUtil).extractRun("token");
        verify(mascotaServices).buscarMascotasPorRun(12345678);
    }

    @Test
    void obtenerMisMascotas_tokenInvalido() {
        when(jwtUtil.extractRun("token")).thenThrow(new RuntimeException("Token invalido"));

        ResponseEntity<?> response = mascotaController.obtenerMisMascotas("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void registrarMascota_DatosValidos() {
        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setChip("ABC123");

        Mascota mascota = new Mascota();
        mascota.setChip("ABC123");

        when(jwtUtil.extractRun("token")).thenReturn(12345678);
        when(mascotaServices.crearMascota(request, null, 12345678)).thenReturn(mascota);

        ResponseEntity<?> response = mascotaController.registrarMascota(
                "Bearer token",
                request,
                null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Mascota) response.getBody()).getChip()).isEqualTo("ABC123");
    }

    @Test
    void registrarMascota_datosInvalidos() {
        CrearMascotaRequest request = new CrearMascotaRequest();

        when(jwtUtil.extractRun("token")).thenReturn(12345678);
        when(mascotaServices.crearMascota(request, null, 12345678))
                .thenThrow(new RuntimeException("Tipo invalido"));

        ResponseEntity<?> response = mascotaController.registrarMascota(
                "Bearer token",
                request,
                null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Tipo invalido");
    }

    @Test
    void eliminarMascota_existente() {
        Mascota mascota = new Mascota();
        mascota.setChip("ABC123");

        when(mascotaServices.buscarUnaMascota("ABC123")).thenReturn(mascota);

        ResponseEntity<?> response = mascotaController.EliminarMascota("ABC123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Mascota) response.getBody()).getChip()).isEqualTo("ABC123");

        verify(mascotaServices).eliminarMascota("ABC123");
    }

    @Test
    void eliminarMascota_noExistente() {
        when(mascotaServices.buscarUnaMascota("NOEXISTE"))
                .thenThrow(new RuntimeException("No existe"));

        ResponseEntity<?> response = mascotaController.EliminarMascota("NOEXISTE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void actualizarMascota_existente() {
        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setNombre("Luna");

        Mascota mascota = new Mascota();
        mascota.setChip("ABC123");
        mascota.setNombre("Luna");

        when(mascotaServices.actualizarMascota("ABC123", request, null)).thenReturn(mascota);

        ResponseEntity<?> response = mascotaController.actualizar("ABC123", request, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Mascota) response.getBody()).getNombre()).isEqualTo("Luna");
    }

    @Test
    void buscarPorRunUsuario() {
        Mascota mascota = new Mascota();
        mascota.setRunUsuario(12345678);

        when(mascotaServices.buscarPorRunUsuario(12345678)).thenReturn(List.of(mascota));

        ResponseEntity<List<Mascota>> response = mascotaController.buscarPorRunUsuario(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void buscarPorRunUsuario_sinMascotas() {
        int run =  12345678;
        when(mascotaServices.buscarPorRunUsuario(run)).thenReturn(List.of());

        ResponseEntity<?> response = mascotaController.buscarPorRunUsuario(run);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).isEmpty();
    }

    }
