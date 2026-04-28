package petly.sanosysalvos.cl.geolocalizacion.Services;



import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoDTO;
import petly.sanosysalvos.cl.geolocalizacion.Model.Localizacion;
import petly.sanosysalvos.cl.geolocalizacion.Repository.GeoRepository;


@Service
public class GeoServices {

    private final GeoRepository repo;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public GeoServices(GeoRepository repo) {
        this.repo = repo;
    }

    // GUARDAR UBICACIÓN
    public void guardar(GeoDTO dto) {

        Point point = geometryFactory.createPoint(
                new Coordinate(dto.getLongitud(), dto.getLatitud())
        );

        Localizacion localizacion = new Localizacion();
        localizacion.setIdReporte(dto.getIdReporte());
        localizacion.setUbicacion(point);
        repo.save(localizacion);
    }

    // OBTENER UBICACIÓN
    public GeoDTO obtenerPorReporte(Long idReporte) {

        Localizacion ub = repo.findByIdReporte(idReporte)
                .orElseThrow(() -> new RuntimeException("No existe ubicación"));

        return new GeoDTO(
                ub.getIdReporte(),
                ub.getUbicacion().getY(), // lat
                ub.getUbicacion().getX()  // lng
        );
    }
}