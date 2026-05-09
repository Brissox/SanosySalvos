package petly.sanosysalvos.cl.notificaciones.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petly.sanosysalvos.cl.notificaciones.Model.Coincidencia;

import java.util.List;
import java.util.Optional;

public interface CoincidenciaRepository extends JpaRepository<Coincidencia, Long> {

    Optional<Coincidencia> findByCoincidenciaIdRef(Long coincidenciaIdRef);

    List<Coincidencia> findByRunUsuarioPerdido(Integer runUsuario);

    List<Coincidencia> findByRunUsuarioEncontrado(Integer runUsuario);
}
