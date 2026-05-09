package petly.sanosysalvos.cl.notificaciones.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    List<Notificacion> findByIdUsuarioAndLeidaOrderByFechaCreacionDesc(Long idUsuario, boolean leida);

    long countByIdUsuarioAndLeida(Long idUsuario, boolean leida);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.idUsuario = :idUsuario AND n.leida = false")
    int marcarTodasComoLeidas(@Param("idUsuario") Long idUsuario);
}
