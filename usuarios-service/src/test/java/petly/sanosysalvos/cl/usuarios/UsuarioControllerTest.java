package petly.sanosysalvos.cl.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import petly.sanosysalvos.cl.usuarios.Controller.usuarioController;
import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Services.usuarioServices;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private usuarioServices usuarioService;

    @InjectMocks
    private usuarioController controller;

    @Test
    void listarUsuarios_conDatos() {
        Usuario usuario = crearUsuario();
        when(usuarioService.BuscarTodoUsuario()).thenReturn(List.of(usuario));

        ResponseEntity<?> response = controller.ListarUsuarios();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);
    }

    @Test
    void listarUsuarios_sinDatos() {
        when(usuarioService.BuscarTodoUsuario()).thenReturn(List.of());

        ResponseEntity<?> response = controller.ListarUsuarios();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se encuentran datos");
    }

    @Test
    void buscarUnUsuario_existente() {
        Usuario usuario = crearUsuario();
        when(usuarioService.BuscarUnUsuario(12345678)).thenReturn(usuario);

        ResponseEntity<?> response = controller.BuscarUnUsuarioPorId(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Usuario) response.getBody()).getRun()).isEqualTo(12345678);
    }

    @Test
    void buscarUnUsuario_noExistente() {
        when(usuarioService.BuscarUnUsuario(12345678))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> response = controller.BuscarUnUsuarioPorId(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se encuentra el usuario");
    }

    @Test
    void guardarUsuario_valido() {
        Usuario usuario = crearUsuario();

        when(usuarioService.GuardarUsuario(usuario)).thenReturn(usuario);

        ResponseEntity<?> response = controller.GuardarUsuario(usuario);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Usuario) response.getBody()).getCorreo()).isEqualTo("test@test.cl");
    }

    @Test
    void guardarUsuario_error() {
        Usuario usuario = crearUsuario();

        when(usuarioService.GuardarUsuario(usuario))
                .thenThrow(new RuntimeException("correo duplicado"));

        ResponseEntity<?> response = controller.GuardarUsuario(usuario);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().toString()).contains("No se puede registrar el usuario");
    }

    @Test
    void eliminarUsuario_existente() {
        Usuario usuario = crearUsuario();

        when(usuarioService.BuscarUnUsuario(12345678)).thenReturn(usuario);

        ResponseEntity<?> response = controller.EliminarUsuario(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Usuario) response.getBody()).getRun()).isEqualTo(12345678);

        verify(usuarioService).EliminarUsuario(12345678);
    }

    @Test
    void eliminarUsuario_noExistente() {
        when(usuarioService.BuscarUnUsuario(12345678))
                .thenThrow(new RuntimeException("No existe"));

        ResponseEntity<?> response = controller.EliminarUsuario(12345678);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se puede eliminar el usuario");
    }

    @Test
    void actualizarUsuario_valido() {
        Usuario usuario = crearUsuario();
        usuario.setNombre("Nuevo");

        when(usuarioService.ActualizarUsuario(usuario)).thenReturn(usuario);

        ResponseEntity<?> response = controller.ActualizarUsuario(12345678, usuario);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Usuario) response.getBody()).getRun()).isEqualTo(12345678);
        assertThat(((Usuario) response.getBody()).getNombre()).isEqualTo("Nuevo");
    }

    @Test
    void actualizarUsuario_error() {
        Usuario usuario = crearUsuario();

        when(usuarioService.ActualizarUsuario(usuario))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = controller.ActualizarUsuario(12345678, usuario);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se puede actualizar el usuario");
    }

    @Test
    void filtrarPorRol_valido() {
        Usuario usuario = crearUsuario();

        when(usuarioService.filtrarPorRol("USUARIO")).thenReturn(List.of(usuario));

        ResponseEntity<?> response = controller.filtrarPorRol("USUARIO");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);
    }

    @Test
    void filtrarPorRol_error_devuelveNotFound() {
        when(usuarioService.filtrarPorRol("ADMIN"))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = controller.filtrarPorRol("ADMIN");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("no se encuentran roles");
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRun(12345678);
        usuario.setDv("9");
        usuario.setNombre("Test");
        usuario.setApellido_paterno("Perez");
        usuario.setApellido_materno("Soto");
        usuario.setTelefono("912345678");
        usuario.setDireccion("Calle 123");
        usuario.setCorreo("test@test.cl");
        usuario.setContrasena("1234");
        return usuario;
    }
}