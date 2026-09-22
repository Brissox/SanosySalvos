package petly.sanosysalvos.cl.usuarios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Repository.usuarioRepository;
import petly.sanosysalvos.cl.usuarios.Services.usuarioServices;

@ExtendWith(MockitoExtension.class)
class usuarioServicesTest {

    @Mock
    private usuarioRepository usuariosrepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private usuarioServices service;

    @Test
    void buscarTodoUsuario() {
        Usuario usuario = crearUsuario();
        when(usuariosrepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = service.BuscarTodoUsuario();

        assertThat(resultado).hasSize(1);
        verify(usuariosrepository).findAll();
    }

    @Test
    void buscarUnUsuario_existente() {
        Usuario usuario = crearUsuario();
        when(usuariosrepository.findById(12345678)).thenReturn(Optional.of(usuario));

        Usuario resultado = service.BuscarUnUsuario(12345678);

        assertThat(resultado.getRun()).isEqualTo(12345678);
    }

    @Test
    void buscarUnUsuario_noExistente() {
        when(usuariosrepository.findById(12345678)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.BuscarUnUsuario(12345678))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void guardarUsuario_conDatos() {
        Usuario usuario = crearUsuario();

        when(passwordEncoder.encode("1234")).thenReturn("hash1234");
        when(usuariosrepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = service.GuardarUsuario(usuario);

        assertThat(resultado.getContrasena()).isEqualTo("hash1234");
        verify(passwordEncoder).encode("1234");
        verify(usuariosrepository).save(usuario);
    }

    @Test
    void guardarUsuario_duplicado() {
        Usuario usuario = crearUsuario();

        when(passwordEncoder.encode("1234")).thenReturn("hash1234");
        when(usuariosrepository.save(usuario))
                .thenThrow(new DataIntegrityViolationException("duplicado"));

        assertThatThrownBy(() -> service.GuardarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El correo o el RUN ya estan registrados");
    }

    @Test
    void eliminarUsuario_conRun() {
        service.EliminarUsuario(12345678);

        verify(usuariosrepository).deleteById(12345678);
    }

    @Test
    void actualizarUsuario_usuarioExistente() {
        Usuario existente = crearUsuario();
        Usuario actualizado = crearUsuario();
        actualizado.setNombre("Nuevo");
        actualizado.setContrasena("nueva123");

        when(usuariosrepository.findById(12345678)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("nueva123")).thenReturn("hashNuevo");
        when(usuariosrepository.save(existente)).thenReturn(existente);

        Usuario resultado = service.ActualizarUsuario(actualizado);

        assertThat(resultado.getNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getContrasena()).isEqualTo("hashNuevo");
        verify(usuariosrepository).save(existente);
    }

    @Test
    void actualizarUsuario_sinCambioContrasena() {
        Usuario existente = crearUsuario();
        existente.setContrasena("hashAntiguo");

        Usuario actualizado = crearUsuario();
        actualizado.setNombre("Nuevo");
        actualizado.setContrasena("");

        when(usuariosrepository.findById(12345678)).thenReturn(Optional.of(existente));
        when(usuariosrepository.save(existente)).thenReturn(existente);

        Usuario resultado = service.ActualizarUsuario(actualizado);

        assertThat(resultado.getNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getContrasena()).isEqualTo("hashAntiguo");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void actualizarUsuario_noExistente() {
        Usuario usuario = crearUsuario();

        when(usuariosrepository.findById(12345678)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ActualizarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al actualizar el usuario");
    }

    @Test
    void filtrarPorRol() {
        Usuario usuario = crearUsuario();

        when(usuariosrepository.findByRolNombreRol("USER")).thenReturn(List.of(usuario));

        List<Usuario> resultado = service.filtrarPorRol("USER");

        assertThat(resultado).hasSize(1);
        verify(usuariosrepository).findByRolNombreRol("USER");
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRun(12345678);
        usuario.setDv("9");
        usuario.setNombre("Juan");
        usuario.setApellido_paterno("Perez");
        usuario.setApellido_materno("Soto");
        usuario.setTelefono("912345678");
        usuario.setDireccion("Calle 123");
        usuario.setCorreo("juan@test.cl");
        usuario.setContrasena("1234");
        return usuario;
    }
}