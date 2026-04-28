package petly.sanosysalvos.cl.reportes.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;

@FeignClient(name = "geolocalizacion-service", url = "http://localhost:8082")
public interface GeoClient {

    @PostMapping("/api/geo")
    void guardarUbicacion(@RequestBody GeoDTO dto);

    @GetMapping("/petly/geo/reporte/{idReporte}")
    GeoDTO obtenerUbicacion(@PathVariable Long idReporte);
}