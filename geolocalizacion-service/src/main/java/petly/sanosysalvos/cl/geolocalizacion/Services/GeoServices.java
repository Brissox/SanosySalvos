package petly.sanosysalvos.cl.geolocalizacion.Services;

import petly.sanosysalvos.cl.dto.GeoDTO;
import petly.sanosysalvos.cl.model.Localizacion;
import petly.sanosysalvos.cl.repository.GeoRepository;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

@Service
public class GeoServices {

    private final GeoRepository repo;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public GeoService(GeoRepository repo) {
        this.repo = repo;
    }

    // GUARDAR UBICACIÓN
    public void guardar(GeoDTO dto) {

        Point point = geometryFactory.createPoint(
                new Coordinate(dto.getLongitud(), dto.getLatitud())
        );

        Ubicacion ubicacion = new Ubicacion(dto.getIdReporte(), point);

        repo.save(ubicacion);
    }

    // OBTENER UBICACIÓN
    public GeoDTO obtenerPorReporte(Long idReporte) {

        Ubicacion ub = repo.findByIdReporte(idReporte)
                .orElseThrow(() -> new RuntimeException("No existe ubicación"));

        return new GeoDTO(
                ub.getIdReporte(),
                ub.getUbicacion().getY(), // lat
                ub.getUbicacion().getX()  // lng
        );
    }
}