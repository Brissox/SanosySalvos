package petly.sanosysalvos.cl.geolocalizacion.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.geolocalizacion.Model.Localizacion;

public interface GeoRepository extends JpaRepository<Localizacion, Long> {

}