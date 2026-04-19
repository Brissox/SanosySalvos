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
import org.springframework.web.bind.annotation.RestController;

import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Services.MascotaServices;


@RestController
@RequestMapping("api/s1/Mascotas")

public class MascotaController {

      @Autowired
    private MascotaServices mascotaService;

    @GetMapping
    public ResponseEntity<?> ListarMascotas() {
        List<Mascota> mascotas = mascotaService.BuscarTodoMascotas();
        if (mascotas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran mascotas");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(mascotas);
        }
    }

    @GetMapping("/{id_mascota}")
    public ResponseEntity<?> BuscarUnaMascotaPorId(@PathVariable Long id_mascota) {
        try{
            Mascota mascota = mascotaService.BuscarUnaMascota(id_mascota);
            return ResponseEntity.status(HttpStatus.OK).body(mascota);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra la mascota");
        }

    }
    @PostMapping
    public ResponseEntity<?> GuardarMascota(@RequestBody Mascota mascotaGuardar) {
        try{
            Mascota mascotaRegistrar = mascotaService.GuardarMascota(mascotaGuardar);
            return ResponseEntity.ok(mascotaRegistrar);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(" no se puede registrar la mascota ");
        }
    }

    @DeleteMapping("/{id_mascota}")
    public ResponseEntity<?> EliminarMascota(@PathVariable Long id_mascota) {
        try{
            Mascota mascotaBuscada = mascotaService.BuscarUnaMascota(id_mascota);
            mascotaService.EliminarMascota(id_mascota);
            return ResponseEntity.ok(mascotaBuscada);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar la mascota ");
        }
    }

    @PutMapping("/{id_mascota}")
    public ResponseEntity<?> ActualizarMascota(@PathVariable Long id_mascota, @RequestBody Mascota mascotaActualizar) {
        try{
            Mascota mascotaActualizada = mascotaService.BuscarUnaMascota(id_mascota);


            mascotaActualizada.setNombre(mascotaActualizar.getNombre());
            mascotaActualizada.setNumero_microchip(mascotaActualizar.getNumero_microchip());
            mascotaActualizada.setEspecie(mascotaActualizar.getEspecie());
            mascotaActualizada.setSexo(mascotaActualizar.getSexo());
            mascotaActualizada.setRaza(mascotaActualizar.getRaza());
            mascotaActualizada.setColor(mascotaActualizar.getColor());
            mascotaActualizada.setFecha_nacimiento(mascotaActualizar.getFecha_nacimiento());
            mascotaActualizada.setEstado_reproductivo(mascotaActualizar.getEstado_reproductivo());

            mascotaService.GuardarMascota(mascotaActualizada);
            return ResponseEntity.ok(mascotaActualizada);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede editar esa mascota");
        }

    }




}
