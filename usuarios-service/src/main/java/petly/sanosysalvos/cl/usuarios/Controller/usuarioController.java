package petly.sanosysalvos.cl.usuarios.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("api/s1/usuarios")
public class usuarioController {
      @Autowired
    private usuarioServices usuarioService;

    @GetMapping
    public ResponseEntity<?> ListarSucursal() {
        List<Usuario> sucursal = usuarioService.BuscarTodoUsuario();
        if (sucursal.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra esa sucursal");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(sucursal);
        }
    }

    @GetMapping("/{idSucursal}")
    public ResponseEntity<?> BuscarUnUsuarioPorId(@PathVariable Long idSucursal) {
        try{
            Usuario sucursal = usuarioService.BuscarUnUsuario(idSucursal);
            return ResponseEntity.status(HttpStatus.OK).body(sucursal);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra esa sucursal");
        }

    }
    @PostMapping
    public ResponseEntity<?> GuardarUsuario(@RequestBody Usuario sucursalGuardar) {
        try{
            Usuario sucursalRegistrar = usuarioService.GuardarUsuario(sucursalGuardar);
            return ResponseEntity.ok(sucursalRegistrar);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("no se encuentra esa sucursal");
        }
    }

    @DeleteMapping("/{idSucursal}")
    public ResponseEntity<?> EliminarUsuario(@PathVariable Long idSucursal) {
        try{
            Usuario sucursalBuscada = usuarioService.BuscarUnUsuario(idSucursal);
            usuarioService.EliminarUsuario(idSucursal);
            return ResponseEntity.ok(sucursalBuscada);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra esa sucursal");
        }
    }

    @PutMapping("/{idSucursal}")
    public ResponseEntity<?> ActualizarSucursal(@PathVariable Long idSucursal, @RequestBody Usuario usuarioActualizar) {
        try{
            Usuario usuarioActualizado = usuarioService.BuscarUnUsuario(idSucursal);

            usuarioActualizado.setNombre(usuarioActualizar.getNombre());
            usuarioActualizado.setApellido_paterno(usuarioActualizar.getApellido_paterno());
            usuarioActualizado.setApellido_materno(usuarioActualizar.getApellido_materno());

            usuarioActualizado.setCorreo(usuarioActualizar.getCorreo());
            usuarioActualizado.setDireccion(usuarioActualizar.getDireccion());
            usuarioActualizado.setTelefono(usuarioActualizar.getTelefono());
            usuarioActualizado.setContrasena(usuarioActualizar.getContrasena());
            usuarioActualizado.setRun(usuarioActualizar.getRun());
            usuarioActualizado.setDv(usuarioActualizar.getDv());

            usuarioService.GuardarUsuario(usuarioActualizado);
            return ResponseEntity.ok(usuarioActualizado);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra esa sucursal");
        }

    }
    
}
