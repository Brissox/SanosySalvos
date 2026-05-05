package petly.sanosysalvos.cl.notificaciones.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;
import petly.sanosysalvos.cl.notificaciones.Services.NotificacionServices;



@RestController
@RequestMapping("/petly/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionServices notificacionservice;

    @GetMapping
    public ResponseEntity<?> ListarNotificaciones() {
        List<Notificacion> notificaciones = notificacionservice.BuscarTodoNotificaciones();
        if (notificaciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran las notificaciones");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(notificaciones);
        }
    }

    @GetMapping("/{id_notificacion}")
    public ResponseEntity<?> BuscarUnaNotificacionPorId(@PathVariable Long id_notificacion) {
        try{
            Notificacion notificacion = notificacionservice.BuscarUnaNotificacion(id_notificacion);
            return ResponseEntity.status(HttpStatus.OK).body(notificacion);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra la notificación");
        }

    }
    
     @PostMapping
    public ResponseEntity<?> GuardarNotificacion(@RequestBody Notificacion notificacionGuardar) {
        try{
            Notificacion notificacionRegistrar = notificacionservice.GuardarNotificacion(notificacionGuardar);
            return ResponseEntity.ok(notificacionRegistrar);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(" no se puede registrar la notificación ");
        }
    }

    @DeleteMapping("/{id_notificacion}")
    public ResponseEntity<?> EliminarNotificacion(@PathVariable Long id_notificacion) {
        try{
            Notificacion notificacionBuscada = notificacionservice.BuscarUnaNotificacion(id_notificacion);
            notificacionservice.EliminarNotificacion(id_notificacion);
            return ResponseEntity.ok(notificacionBuscada);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar la notificación ");
        }
    }

    @PutMapping("/{id_notificacion}")
    public ResponseEntity<?> ActualizarNotificacion(@PathVariable Long id_notificacion, @RequestBody Notificacion notificacionActualizar) {
        try{
            Notificacion notificacionActualizada = notificacionservice.BuscarUnaNotificacion(id_notificacion);

            notificacionActualizada.setTitulo(notificacionActualizar.getTitulo());
            notificacionActualizada.setMensaje(notificacionActualizar.getMensaje());
            notificacionActualizada.setFecha_creacion(notificacionActualizar.getFecha_creacion());
            notificacionActualizada.setLeida(notificacionActualizar.isLeida());
            notificacionActualizada.setTipo(notificacionActualizar.getTipo());
            notificacionActualizada.setId_usuario(notificacionActualizar.getId_usuario());
            notificacionActualizada.setId_reporte(notificacionActualizar.getId_reporte());
            notificacionservice.GuardarNotificacion(notificacionActualizada);
            return ResponseEntity.ok(notificacionActualizada);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede editar la notificación");
        }

    }


}