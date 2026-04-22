package petly.sanosysalvos.cl.usuarios.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Services.usuarioServices;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/petly/usuarios")
public class usuarioController {
      @Autowired
    private usuarioServices usuarioService;


    @GetMapping
    public ResponseEntity<?> ListarUsuarios() {
        List<Usuario> usuario = usuarioService.BuscarTodoUsuario();
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran datos");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        }
    }

    @GetMapping("/{id_usuario}")
    public ResponseEntity<?> BuscarUnUsuarioPorId(@PathVariable Long id_usuario) {
        try{
            Usuario usuario = usuarioService.BuscarUnUsuario(id_usuario);
            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra el usuario");
        }

    }
    @PostMapping
    public ResponseEntity<?> GuardarUsuario(@RequestBody Usuario usuarioGuardar) {
        try{
            Usuario usuarioRegistrar = usuarioService.GuardarUsuario(usuarioGuardar);
            return ResponseEntity.ok(usuarioRegistrar);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede registrar el usuario: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id_usuario}")
    public ResponseEntity<?> EliminarUsuario(@PathVariable Long id_usuario) {
        try{
            Usuario usuarioBuscado = usuarioService.BuscarUnUsuario(id_usuario);
            usuarioService.EliminarUsuario(id_usuario);
            return ResponseEntity.ok(usuarioBuscado);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar el usuario");
        }
    }

    @PutMapping("/{id_usuario}")
    public ResponseEntity<?> ActualizarUsuario(@PathVariable Long id_usuario, @RequestBody Usuario usuarioActualizar) {
        try{
            usuarioActualizar.setId_usuario(id_usuario);
            Usuario usuarioActualizado = usuarioService.ActualizarUsuario(usuarioActualizar);
            return ResponseEntity.ok(usuarioActualizado);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede actualizar el usuario");
        }
    }
    
}
