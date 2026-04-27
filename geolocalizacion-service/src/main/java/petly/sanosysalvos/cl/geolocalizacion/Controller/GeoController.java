package petly.sanosysalvos.cl.geolocalizacion.Controller;

@RestController
@RequestMapping("/petly/geolocalizacion")
public class GeoController {

    private final GeoService service;

    public GeoController(GeoService service) {
        this.service = service;
    }

    @PostMapping 
    // Endpoint POST /Geos
    public GeoResponse create(@RequestBody GeoRequest request) {
        return service.create(request); 
        // Crea una nueva ubicación
    }

    @GetMapping("/{id}") 
    // Endpoint GET /Geos/{id}
    public GeoResponse getById(@PathVariable Long id) {
        return service.getById(id); 
        // Obtiene ubicación por ID
    }

    @GetMapping("/near") 
    // Endpoint GET /Geos/near?lat=...&lng=...&radius=...
    public List<GeoResponse> findNearby(
        @RequestParam double lat, 
        // Latitud de referencia

        @RequestParam double lng, 
        // Longitud de referencia

        @RequestParam double radius 
        // Radio en metros
    ) {
        return service.findNearby(lat, lng, radius); 
        // Retorna ubicaciones cercanas
    }
}