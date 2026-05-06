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
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{run}")
    public ResponseEntity<?> BuscarUnUsuarioPorId(@PathVariable Integer run) {
        try{
            Usuario usuario = usuarioService.BuscarUnUsuario(run);
            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra el usuario");
        }

    }
    
    @PostMapping("/registrar")
    public ResponseEntity<?> GuardarUsuario(@RequestBody Usuario usuarioGuardar) {
        try{
            Usuario usuarioRegistrar = usuarioService.GuardarUsuario(usuarioGuardar);
            return ResponseEntity.ok(usuarioRegistrar);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede registrar el usuario: " + e.getMessage());
        }
    }

    @DeleteMapping("/{run}")
    public ResponseEntity<?> EliminarUsuario(@PathVariable Integer run) {
        try{
            Usuario usuarioBuscado = usuarioService.BuscarUnUsuario(run);
            usuarioService.EliminarUsuario(run);
            return ResponseEntity.ok(usuarioBuscado);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar el usuario");
        }
    }

    @PutMapping("/{run}")
    public ResponseEntity<?> ActualizarUsuario(@PathVariable Integer run, @RequestBody Usuario usuarioActualizar) {
        try{
            usuarioActualizar.setRun(run);
            Usuario usuarioActualizado = usuarioService.ActualizarUsuario(usuarioActualizar);
            return ResponseEntity.ok(usuarioActualizado);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede actualizar el usuario");
        }
    }

    
    @GetMapping("/filtrar/rol")
    public ResponseEntity<?> filtrarPorRol(@RequestParam String nombreRol) {
        try{
            List<Usuario> usuarios = usuarioService.filtrarPorRol(nombreRol);
            return ResponseEntity.status(HttpStatus.OK).body(usuarios);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran roles");
        }
    }
}
