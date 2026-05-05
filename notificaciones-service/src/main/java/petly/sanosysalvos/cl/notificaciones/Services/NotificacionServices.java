package petly.sanosysalvos.cl.notificaciones.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;
import petly.sanosysalvos.cl.notificaciones.Repository.NotificacionRepository;


@Service
@Transactional

public class NotificacionServices {

     @Autowired

    private NotificacionRepository notificacionrepository;

    public List<Notificacion> BuscarTodoNotificaciones(){
        return notificacionrepository.findAll();
    }

    public Notificacion BuscarUnaNotificacion(Long id_notificacion){
        return notificacionrepository.findById(id_notificacion).get();

    }

    public Notificacion GuardarNotificacion(Notificacion Notificacion){
        return notificacionrepository.save(Notificacion);

    }

    public void EliminarNotificacion(Long id_notificacion){
        notificacionrepository.deleteById(id_notificacion);
    }
    
}