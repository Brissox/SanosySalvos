package petly.sanosysalvos.cl.notificaciones.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
}
