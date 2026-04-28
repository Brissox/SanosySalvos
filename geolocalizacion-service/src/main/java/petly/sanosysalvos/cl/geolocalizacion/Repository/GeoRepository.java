package petly.sanosysalvos.cl.geolocalizacion.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.geolocalizacion.Model.Localizacion;

public interface GeoRepository extends JpaRepository<Localizacion, Long> {

    Optional<Localizacion> findByIdReporte(Long idReporte);
}