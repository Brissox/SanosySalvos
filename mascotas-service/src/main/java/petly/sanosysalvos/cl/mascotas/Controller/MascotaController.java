package petly.sanosysalvos.cl.mascotas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import petly.sanosysalvos.cl.mascotas.Config.JwtUtil;
import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Services.MascotaServices;
import petly.sanosysalvos.cl.mascotas.Services.OracleStorageService;

@RestController
@RequestMapping("/petly/mascotas")

public class MascotaController {

    @Autowired
    private MascotaServices mascotaService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> ListarMascotas() {
        List<Mascota> mascotas = mascotaService.BuscarTodoMascotas();
        if (mascotas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran mascotas");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(mascotas);
        }
    }

    @GetMapping("/{id_mascota}")
    public ResponseEntity<?> BuscarUnaMascotaPorId(@PathVariable String chip) {
        try {
            Mascota mascota = mascotaService.BuscarUnaMascota(chip);
            return ResponseEntity.status(HttpStatus.OK).body(mascota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra la mascota");
        }

    }

    @PostMapping(value = "/registrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarMascota(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile foto) {
        try {
            // 1. Extraer el userId desde el token JWT
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);

            // 2. Parsear el JSON con los datos de la mascota
            ObjectMapper mapper = new ObjectMapper();
            MascotaRequest mascotaRequest = mapper.readValue(dataJson, MascotaRequest.class);

            // 3. Registrar la mascota asociada al usuario
            Mascota mascotaGuardada = mascotaService.registrar(mascotaRequest, foto, userId);

            return ResponseEntity.ok(mascotaGuardada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar mascota: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id_mascota}")
    public ResponseEntity<?> EliminarMascota(@PathVariable String chip) {
        try {
            Mascota mascotaBuscada = mascotaService.BuscarUnaMascota(chip);
            mascotaService.EliminarMascota(chip);
            return ResponseEntity.ok(mascotaBuscada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar la mascota ");
        }
    }

    @PutMapping(value = "/{id_mascota}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizar(
            @PathVariable Long id_mascota,
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        try {
            Mascota mascota = mascotaService.actualizarMascota(id_mascota, request, imagen);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se puede editar esa mascota");
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crear(
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart("imagen") MultipartFile imagen) {

        try {
            Mascota mascota = mascotaService.crearMascota(request, imagen);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear mascota");
        }
    }
}
