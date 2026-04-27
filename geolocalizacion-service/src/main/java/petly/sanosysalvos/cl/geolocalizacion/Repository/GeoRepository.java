package petly.sanosysalvos.cl.geolocalizacion.repository;

import petly.sanosysalvos.cl.model.Localizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeoRepository extends JpaRepository<Localizacion, Long> {

    Optional<Localizacion> findByIdReporte(Long idReporte);
}