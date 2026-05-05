package petly.sanosysalvos.cl.mascotas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petly.sanosysalvos.cl.mascotas.Config.JwtUtil;
import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Services.MascotaServices;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/petly/mascotas")

public class MascotaController {

    @Autowired
    private MascotaServices mascotaService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> ListarMascotas() {
        List<Mascota> mascotas = mascotaService.buscarTodoMascotas();
        if (mascotas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran mascotas");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(mascotas);
        }
    }

    @GetMapping("/{id_mascota}")
    public ResponseEntity<?> BuscarUnaMascotaPorId(@PathVariable String chip) {
        try {
            Mascota mascota = mascotaService.buscarUnaMascota(chip);
            return ResponseEntity.status(HttpStatus.OK).body(mascota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra la mascota");
        }

    }

    @GetMapping("/mis-mascotas")
    public ResponseEntity<?> obtenerMisMascotas(
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            Integer run = jwtUtil.extractRun(token);

            List<Mascota> mascotas = mascotaService.buscarMascotasPorRun(run);

            return ResponseEntity.ok(mascotas);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o error al obtener mascotas");
        }
    }

    @PostMapping(value = "/registrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarMascota(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("data") MascotaRequest mascotaRequest,
            @RequestPart(value = "file", required = false) MultipartFile foto) {

        try {
            String token = authHeader.replace("Bearer ", "");
            Integer run = jwtUtil.extractRun(token);

            Mascota mascotaGuardada = mascotaService.crearMascota(mascotaRequest, foto, run);

            return ResponseEntity.ok(mascotaGuardada);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno");
        }
    }

    @DeleteMapping("/{id_mascota}")
    public ResponseEntity<?> EliminarMascota(@PathVariable String chip) {
        try {
            Mascota mascotaBuscada = mascotaService.buscarUnaMascota(chip);
            mascotaService.eliminarMascota(chip);
            return ResponseEntity.ok(mascotaBuscada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar la mascota ");
        }
    }

    @PutMapping(value = "/{id_mascota}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizar(
            @PathVariable String chip,
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        try {
            Mascota mascota = mascotaService.actualizarMascota(chip, request, imagen);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se puede editar esa mascota");
        }
    }

}