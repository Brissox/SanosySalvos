package petly.sanosysalvos.cl.geolocalizacion.Services;


import java.util.List;
import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoDTO;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoRequest;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoResponse;
import petly.sanosysalvos.cl.geolocalizacion.Model.Localizacion;
import petly.sanosysalvos.cl.geolocalizacion.Repository.GeoRepository;


@Service
public class GeoServices {

    private final GeoRepository repo;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public GeoServices(GeoRepository repo) {
        this.repo = repo;
    }

    public GeoResponse crear(GeoRequest dto) {

            Point point = geometryFactory.createPoint(
                new Coordinate(dto.getLongitud(), dto.getLatitud())
            );

            Localizacion geo = new Localizacion();
            geo.setUbicacion(point);

            geo = repo.save(geo);

            GeoResponse res = new GeoResponse();
            res.setId(geo.getId());

            return res;
        }

     public GeoDTO obtener(Long id) {

        Localizacion geo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Geo no encontrada"));

        GeoDTO dto = new GeoDTO();
        dto.setId(geo.getId());
        dto.setLatitud(geo.getUbicacion().getY());
        dto.setLongitud(geo.getUbicacion().getX());

        return dto;
    }

    
    public List<GeoDTO> buscarTodos() {
    return repo.findAll().stream()
        .map(geo -> {
            if (geo.getUbicacion() instanceof org.locationtech.jts.geom.Point point) {
                return new GeoDTO(
                    geo.getId(),
                    point.getY(), // latitud
                    point.getX()  // longitud
                );
            }
            return null; // o manejar mejor el caso
        })
        .filter(Objects::nonNull)
        .toList();
    }

     public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("No existe la geolocalización");
        }
        repo.deleteById(id);
    }

    public void eliminarDTO(Long id) {
    if (!repo.existsById(id)) {
        throw new RuntimeException("Localización no encontrada");
    }

    repo.deleteById(id);
}



}