package petly.sanosysalvos.cl.notificaciones.Controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petly.sanosysalvos.cl.notificaciones.Config.JwtUtil;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;

import java.util.Map;

@RestController
@RequestMapping("/petly/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionServices notificacionServices;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> listarPorUsuario(@RequestHeader("Authorization") String authHeader) {
        try {
            Long idUsuario = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            return ResponseEntity.ok(notificacionServices.buscarPorUsuario(idUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificacionServices.buscarPorId(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<?> buscarNoLeidas(@RequestHeader("Authorization") String authHeader) {
        try {
            Long idUsuario = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            return ResponseEntity.ok(notificacionServices.buscarNoLeidas(idUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @GetMapping("/contador")
    public ResponseEntity<?> contarNoLeidas(@RequestHeader("Authorization") String authHeader) {
        try {
            Long idUsuario = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            return ResponseEntity.ok(Map.of("noLeidas", notificacionServices.contarNoLeidas(idUsuario)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificacionServices.marcarComoLeida(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/leer-todas")
    public ResponseEntity<?> marcarTodasComoLeidas(@RequestHeader("Authorization") String authHeader) {
        try {
            Long idUsuario = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            return ResponseEntity.ok(Map.of("actualizadas", notificacionServices.marcarTodasComoLeidas(idUsuario)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            notificacionServices.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
