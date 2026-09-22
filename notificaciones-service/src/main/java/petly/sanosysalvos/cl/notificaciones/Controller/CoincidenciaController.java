package petly.sanosysalvos.cl.notificaciones.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/usuario/{runUsuario}")
    public ResponseEntity<List<Coincidencia>> buscarPorUsuario(@PathVariable Integer runUsuario) {
        return ResponseEntity.ok(coincidenciaService.buscarPorUsuario(runUsuario));
    }
}
