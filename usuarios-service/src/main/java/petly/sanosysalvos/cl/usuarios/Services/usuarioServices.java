package petly.sanosysalvos.cl.usuarios.Services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Repository.usuarioRepository;

@Service
@Transactional

public class usuarioServices {

    private final usuarioRepository usuariosrepository;
    private final PasswordEncoder passwordEncoder;

    public usuarioServices(usuarioRepository usuariosrepository, PasswordEncoder passwordEncoder) {
        this.usuariosrepository = usuariosrepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> BuscarTodoUsuario() {
        return usuariosrepository.findAll();
    }

    public Usuario BuscarUnUsuario(Integer RUN_USUARIO) {
        return usuariosrepository.findById(RUN_USUARIO).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario GuardarUsuario(Usuario usuario) {

        try
        {
            String passwordHasheada = passwordEncoder.encode(usuario.getContrasena());
            usuario.setContrasena(passwordHasheada);
            if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
                throw new RuntimeException("La contraseña no puede estar vacía");
            }
        
            return usuariosrepository.save(usuario);
            
        }catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El correo o el RUN ya estan registrados", e);
        }catch(Exception e){
            throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
        }

    }

    public void EliminarUsuario(Integer ID_USUARIO) {
        usuariosrepository.deleteById(ID_USUARIO);
    }

    public Usuario ActualizarUsuario(Usuario usuario) {

        try {
            Usuario usuarioExistente = usuariosrepository.findById(usuario.getRun()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido_paterno(usuario.getApellido_paterno());
            usuarioExistente.setApellido_materno(usuario.getApellido_materno());
            usuarioExistente.setTelefono(usuario.getTelefono());
            usuarioExistente.setDireccion(usuario.getDireccion());
            usuarioExistente.setCorreo(usuario.getCorreo());
            usuarioExistente.setRun(usuario.getRun());
            usuarioExistente.setDv(usuario.getDv());

             if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
                String nuevaHash = passwordEncoder.encode(usuario.getContrasena());
                usuarioExistente.setContrasena(nuevaHash);
            }

            return usuariosrepository.save(usuarioExistente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el usuario", e);
        }

    }


}