package petly.sanosysalvos.cl.notificaciones.Services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petly.sanosysalvos.cl.notificaciones.Model.Notificacion;
import petly.sanosysalvos.cl.notificaciones.Model.TipoNotificacion;
import petly.sanosysalvos.cl.notificaciones.Repository.NotificacionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServices {

    private final NotificacionRepository notificacionRepository;

    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAll();
    }

    public Notificacion buscarPorId(Long id) {
        return notificacionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notificacion no encontrada con ID: " + id));
    }

    public List<Notificacion> buscarPorUsuario(Long idUsuario) {
        return notificacionRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    public List<Notificacion> buscarNoLeidas(Long idUsuario) {
        return notificacionRepository.findByIdUsuarioAndLeidaOrderByFechaCreacionDesc(idUsuario, false);
    }

    public long contarNoLeidas(Long idUsuario) {
        return notificacionRepository.countByIdUsuarioAndLeida(idUsuario, false);
    }

    @Transactional
    public Notificacion marcarComoLeida(Long id) {
        Notificacion notificacion = buscarPorId(id);
        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public int marcarTodasComoLeidas(Long idUsuario) {
        return notificacionRepository.marcarTodasComoLeidas(idUsuario);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Notificacion no encontrada con ID: " + id);
        }
        notificacionRepository.deleteById(id);
    }

    @Transactional
    public Notificacion crearNotificacionCoincidencia(
            Long idUsuario,
            Long idReporte,
            Long idCoincidencia,
            String titulo,
            String mensaje) {
        Notificacion notificacion = Notificacion.builder()
            .titulo(titulo)
            .mensaje(mensaje)
            .fechaCreacion(LocalDateTime.now())
            .leida(false)
            .tipo(TipoNotificacion.COINCIDENCIA_POTENCIAL)
            .idUsuario(idUsuario)
            .idReporte(idReporte)
            .idCoincidencia(idCoincidencia)
            .build();
        return notificacionRepository.save(notificacion);
    }
}
