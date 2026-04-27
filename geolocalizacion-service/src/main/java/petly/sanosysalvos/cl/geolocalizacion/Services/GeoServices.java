package petly.sanosysalvos.cl.geolocalizacion.Services;

@Service // Marca esta clase como servicio (lógica de negocio)
public class LocationService {

    private final LocationRepository repo; 
    // Repositorio para acceder a la BD

    private final GeometryFactory geometryFactory = new GeometryFactory(); 
    // Factory para crear objetos geométricos (Point)

    public LocationService(LocationRepository repo) {
        this.repo = repo;
    }

    public LocationResponse create(LocationRequest request) {

        // Crea un punto con coordenadas (lng, lat)
        // IMPORTANTE: el orden es longitud primero, luego latitud
        Point point = geometryFactory.createPoint(
            new Coordinate(request.getLng(), request.getLat())
        );

        point.setSRID(4326); 
        // Define el sistema de coordenadas (GPS estándar)

        Location loc = new Location(); 
        // Crea nueva entidad

        loc.setGeom(point); 
        // Asigna el punto geométrico

        loc = repo.save(loc); 
        // Guarda en la base de datos

        return new LocationResponse(
            loc.getId(), 
            // ID generado en la BD

            request.getLat(), 
            // Devuelve latitud

            request.getLng() 
            // Devuelve longitud
        );
    }

    public LocationResponse getById(Long id) {

        Location loc = repo.findById(id)
                .orElseThrow(); 
        // Busca por ID o lanza excepción si no existe

        return new LocationResponse(
            loc.getId(),

            loc.getGeom().getY(), 
            // Y = latitud

            loc.getGeom().getX() 
            // X = longitud
        );
    }

    public List<LocationResponse> findNearby(double lat, double lng, double radius) {

        return repo.findNearby(lat, lng, radius)
                .stream()
                .map(loc -> new LocationResponse(
                        loc.getId(),

                        loc.getGeom().getY(), 
                        // Latitud

                        loc.getGeom().getX() 
                        // Longitud
                ))
                .collect(Collectors.toList());
        // Convierte lista de entidades a lista de DTOs
    }
}