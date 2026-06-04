package petly.sanosysalvos.cl.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import petly.sanosysalvos.cl.usuarios.Config.JwtUtil;
import petly.sanosysalvos.cl.usuarios.Controller.AuthController;
import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Repository.usuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private usuarioRepository usuarioRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController controller;

    @Test
    void login_credencialesValidas() {
        Usuario usuario = new Usuario();
        usuario.setRun(12345678);
        usuario.setCorreo("test@test.cl");
        usuario.setNombre("Test");
        usuario.setContrasena("hash");

        when(usuarioRepo.findByCorreo("test@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash")).thenReturn(true);
        when(jwtUtil.generarToken("test@test.cl", 12345678)).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.login(Map.of(
                "correo", "test@test.cl",
                "contrasena", "1234"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).contains("jwt-token");
    }

    @Test
    void login_usuarioNoExiste() {
        when(usuarioRepo.findByCorreo("no@test.cl")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.login(Map.of(
                "correo", "no@test.cl",
                "contrasena", "1234"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_passwordIncorrecta() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@test.cl");
        usuario.setContrasena("hash");

        when(usuarioRepo.findByCorreo("test@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        ResponseEntity<?> response = controller.login(Map.of(
                "correo", "test@test.cl",
                "contrasena", "mala"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}