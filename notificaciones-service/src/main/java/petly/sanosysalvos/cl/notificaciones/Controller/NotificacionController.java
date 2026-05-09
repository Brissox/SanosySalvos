package petly.sanosysalvos.cl.notificaciones.Controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/petly/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionServices notificacionServices;

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarTodas() {
        return ResponseEntity.ok(notificacionServices.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificacionServices.buscarPorId(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Notificacion>> buscarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(notificacionServices.buscarPorUsuario(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/no-leidas")
    public ResponseEntity<List<Notificacion>> buscarNoLeidas(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(notificacionServices.buscarNoLeidas(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/contador")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(Map.of("noLeidas", notificacionServices.contarNoLeidas(idUsuario)));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificacionServices.marcarComoLeida(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/usuario/{idUsuario}/leer-todas")
    public ResponseEntity<Map<String, Integer>> marcarTodasComoLeidas(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(Map.of("actualizadas", notificacionServices.marcarTodasComoLeidas(idUsuario)));
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
