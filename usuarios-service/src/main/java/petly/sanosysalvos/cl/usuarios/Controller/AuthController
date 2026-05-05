package petly.sanosysalvos.cl.usuarios.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petly.sanosysalvos.cl.usuarios.Config.JwtUtil;
import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Repository.usuarioRepository;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/petly/auth")
public class AuthController {

    private final usuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(usuarioRepository usuarioRepo,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login") //Endpoint para iniciar sesión
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo"); //Email del usuario que intenta iniciar sesión
        String contrasena = credenciales.get("contrasena"); //Contraseña del usuario que intenta iniciar sesión 

        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo); //Busca usuario por email en la base de datos

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) { //Verifica contraseña ingresada con la contraseña almacenada (que está hasheada)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        //Genera token JWT para el usuario autenticado
        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRun());
        //Respuesta exito
        return ResponseEntity.ok(Map.of(
                "token", token,
                "correo", usuario.getCorreo(),
                "nombre", usuario.getNombre()
        ));
    }
}