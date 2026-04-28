/*package petly.sanosysalvos.cl.geolocalizacion.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoRequest;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoResponse;
import petly.sanosysalvos.cl.geolocalizacion.Model.Geolocalizacion;
import petly.sanosysalvos.cl.geolocalizacion.Repository.GeoRepository;

@Service // Marca esta clase como servicio (lógica de negocio)
public class GeoService {

    private final GeoRepository repo; 
    // Repositorio para acceder a la BD

    private final GeometryFactory geometryFactory = new GeometryFactory(); 
    // Factory para crear objetos geométricos (Point)

    public GeoService(GeoRepository repo) {
        this.repo = repo;
    }

    public GeoResponse create(GeoRequest request) {

        // Crea un punto con coordenadas (lng, lat)
        // IMPORTANTE: el orden es longitud primero, luego latitud
        org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
            new Coordinate(request.getLng(), request.getLat())
        );

        point.setSRID(4326); 
        // Define el sistema de coordenadas (GPS estándar)

        Geolocalizacion loc = new Geolocalizacion(); 
        // Crea nueva entidad

        loc.setGeom(point); 
        // Asigna el punto geométrico

        loc = repo.save(loc); 
        // Guarda en la base de datos

        return new GeoResponse(
            loc.getId(),    
            // ID generado en la BD

            request.getLat(), 
            // Devuelve latitud

            request.getLng() 
            // Devuelve longitud
        );
    }

    public GeoResponse getById(Long id) {

        Geolocalizacion loc = repo.findById(id)
                .orElseThrow(); 
        // Busca por ID o lanza excepción si no existe

        return new GeoResponse(
            loc.getId(),

            loc.getGeom().getY(), 
            // Y = latitud

            loc.getGeom().getX() 
            // X = longitud
        );
    }

    public List<GeoResponse> findNearby(double lat, double lng, double radius) {

        return repo.findNearby(lat, lng, radius)
                .stream()
                .map(loc -> new GeoResponse(
                        loc.getId(),

                        loc.getGeom().getY(), 
                        // Latitud

                        loc.getGeom().getX() 
                        // Longitud
                ))
                .collect(Collectors.toList());
        // Convierte lista de entidades a lista de DTOs
    }
}*/