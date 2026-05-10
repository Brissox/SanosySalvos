package petly.sanosysalvos.cl.geolocalizacion.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoDTO;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoRequest;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoResponse;
import petly.sanosysalvos.cl.geolocalizacion.Model.Localizacion;
import petly.sanosysalvos.cl.geolocalizacion.Repository.GeoRepository;

@Service
public class GeoServices {

    private final GeoRepository repo;

    public GeoServices(GeoRepository repo) {
        this.repo = repo;
    }

    public GeoResponse crear(GeoRequest dto) {
        Localizacion geo = new Localizacion();
        geo.setLatitud(dto.getLatitud());
        geo.setLongitud(dto.getLongitud());
        geo = repo.save(geo);

        GeoResponse res = new GeoResponse();
        res.setId(geo.getId());
        return res;
    }

    public GeoDTO obtener(Long id) {
        Localizacion geo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Geo no encontrada"));
        return new GeoDTO(geo.getId(), geo.getLatitud(), geo.getLongitud());
    }

    public List<GeoDTO> buscarTodos() {
        return repo.findAll().stream()
                .map(geo -> new GeoDTO(geo.getId(), geo.getLatitud(), geo.getLongitud()))
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
