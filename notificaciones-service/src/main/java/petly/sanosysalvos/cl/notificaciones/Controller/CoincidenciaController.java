package petly.sanosysalvos.cl.notificaciones.Controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petly.sanosysalvos.cl.notificaciones.Model.Coincidencia;
import petly.sanosysalvos.cl.notificaciones.Services.CoincidenciaService;

import java.util.List;

@RestController
@RequestMapping("/petly/coincidencias")
@RequiredArgsConstructor
public class CoincidenciaController {

    private final CoincidenciaService coincidenciaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(coincidenciaService.buscarPorId(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{runUsuario}")
    public ResponseEntity<List<Coincidencia>> buscarPorUsuario(@PathVariable Integer runUsuario) {
        return ResponseEntity.ok(coincidenciaService.buscarPorUsuario(runUsuario));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(coincidenciaService.confirmarCoincidencia(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(coincidenciaService.rechazarCoincidencia(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
