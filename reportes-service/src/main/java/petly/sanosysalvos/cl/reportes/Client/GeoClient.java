package petly.sanosysalvos.cl.reportes.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.GeoRequest;
import petly.sanosysalvos.cl.reportes.DTO.GeoResponse;


@FeignClient(name = "geolocalizacion-service", url = "http://localhost:8082")
public interface GeoClient {

    @PostMapping("/petly/geo")
    GeoResponse crear(@RequestBody GeoRequest request);

    @GetMapping("/petly/geo/{id}")
    GeoDTO obtener(@PathVariable Long id);

    @DeleteMapping("/petly/geo/{id}")
    void eliminarDTO(@PathVariable Long id);

}