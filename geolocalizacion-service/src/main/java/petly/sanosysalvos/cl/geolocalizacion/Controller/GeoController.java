package petly.sanosysalvos.cl.geolocalizacion.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/petly/geo")
public class GeoController {

 private final GeoService service;

    public GeoController(GeoService service) {
        this.service = service;
    }

    //guardar ubicación
    @PostMapping
    public void guardar(@RequestBody GeoDTO dto) {
        service.guardar(dto);
    }

    //obtener por reporte
    @GetMapping("/reporte/{idReporte}")
    public GeoDTO obtener(@PathVariable Long idReporte) {
        return service.obtenerPorReporte(idReporte);
    }

/*
    private final GeoService service;

    @GetMapping("/reporte/{idReporte}")
    public GeoDTO getByReporte(@PathVariable Long idReporte) {
        return service.findByReporte(idReporte);
    }
*/

}