package petly.sanosysalvos.cl.geolocalizacion.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoDTO;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoRequest;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoResponse;
import petly.sanosysalvos.cl.geolocalizacion.Services.GeoServices;

@RestController
@RequestMapping("/petly/geo")
public class GeoController {

private final GeoServices service;

    public GeoController(GeoServices service) {
        this.service = service;
    }


    @PostMapping
    public GeoResponse crear(@RequestBody GeoRequest ubicacion) {
        return service.crear(ubicacion);
    }
    
    @GetMapping("/{id}")
    public GeoDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
  

    @GetMapping("/todos")
    public ResponseEntity<List<GeoDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @DeleteMapping("/e/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDTO(@PathVariable Long id) {
        service.eliminarDTO(id);
        return ResponseEntity.noContent().build();
    }


}