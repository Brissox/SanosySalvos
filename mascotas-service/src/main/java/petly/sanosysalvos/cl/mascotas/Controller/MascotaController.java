package petly.sanosysalvos.cl.mascotas.Controller;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Services.MascotaServices;
import petly.sanosysalvos.cl.mascotas.Services.OracleStorageService;

@RestController
@RequestMapping("/petly/mascotas")

public class MascotaController {

    @Autowired
    private MascotaServices mascotaService;

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
    public ResponseEntity<?> BuscarUnaMascotaPorId(@PathVariable Long id_mascota) {
        try {
            Mascota mascota = mascotaService.BuscarUnaMascota(id_mascota);
            return ResponseEntity.status(HttpStatus.OK).body(mascota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra la mascota");
        }

    }
    /*
     * @PostMapping
     * public ResponseEntity<?> GuardarMascota(@RequestBody Mascota mascotaGuardar)
     * {
     * try{
     * Mascota mascotaRegistrar = mascotaService.GuardarMascota(mascotaGuardar);
     * return ResponseEntity.ok(mascotaRegistrar);
     * }catch(Exception e){
     * return ResponseEntity.status(HttpStatus.CONFLICT).
     * body(" no se puede registrar la mascota ");
     * }
     * }
     */

    @DeleteMapping("/{id_mascota}")
    public ResponseEntity<?> EliminarMascota(@PathVariable Long id_mascota) {
        try {
            Mascota mascotaBuscada = mascotaService.BuscarUnaMascota(id_mascota);
            mascotaService.EliminarMascota(id_mascota);
            return ResponseEntity.ok(mascotaBuscada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar la mascota ");
        }
    }

    @PutMapping(value = "/{id_mascota}", consumes = "multipart/form-data")
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

    @PostMapping(consumes = "multipart/form-data")
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
