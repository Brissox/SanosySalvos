package petly.sanosysalvos.cl.reportes.Client;

@FeignClient(name = "geolocalizacion-service", url = "http://localhost:8082")
public interface GeoClient {

    @PostMapping("/api/geo")
    void guardarUbicacion(@RequestBody GeoDTO dto);

    @GetMapping("/petly/geo/reporte/{idReporte}")
    GeoDTO obtenerUbicacion(@PathVariable Long idReporte);
}