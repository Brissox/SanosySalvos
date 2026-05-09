package petly.sanosysalvos.cl.notificaciones.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import petly.sanosysalvos.cl.notificaciones.DTO.CoincidenciaEventoDTO;
import petly.sanosysalvos.cl.notificaciones.DTO.ReporteDetalleDTO;
import petly.sanosysalvos.cl.notificaciones.Model.Coincidencia;
import petly.sanosysalvos.cl.notificaciones.Model.EstadoCoincidencia;
import petly.sanosysalvos.cl.notificaciones.Repository.CoincidenciaRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CoincidenciaService {

    private final CoincidenciaRepository coincidenciaRepository;
    private final NotificacionServices notificacionServices;
    private final RestTemplate restTemplate;

    @Value("${services.reportes.url}")
    private String reportesUrl;

    public void procesarEvento(CoincidenciaEventoDTO evento) {
        if (coincidenciaRepository.findByCoincidenciaIdRef(evento.getCoincidenciaId()).isPresent()) {
            log.info("Coincidencia {} ya procesada, ignorando duplicado", evento.getCoincidenciaId());
            return;
        }

        Integer runUsuarioPerdido = null;
        Integer runUsuarioEncontrado = null;
        try {
            runUsuarioPerdido = obtenerReporte(evento.getReportePerdidoId()).getRunUsuario();
            runUsuarioEncontrado = obtenerReporte(evento.getReporteEncontradoId()).getRunUsuario();
        } catch (Exception e) {
            log.warn("No se pudo obtener usuarios desde reportes-service: {}", e.getMessage());
        }

        Coincidencia coincidencia = Coincidencia.builder()
            .coincidenciaIdRef(evento.getCoincidenciaId())
            .reportePerdidoId(evento.getReportePerdidoId())
            .reporteEncontradoId(evento.getReporteEncontradoId())
            .score(evento.getScore())
            .estado(EstadoCoincidencia.PENDIENTE)
            .runUsuarioPerdido(runUsuarioPerdido)
            .runUsuarioEncontrado(runUsuarioEncontrado)
            .fechaCreacion(LocalDateTime.now())
            .build();

        Coincidencia saved = coincidenciaRepository.save(coincidencia);
        int porcentaje = (int) Math.round(evento.getScore() * 100);

        if (runUsuarioPerdido != null) {
            notificacionServices.crearNotificacionCoincidencia(
                runUsuarioPerdido.longValue(),
                evento.getReportePerdidoId(),
                saved.getId(),
                "Posible coincidencia encontrada",
                "Encontramos un reporte que podria coincidir con tu mascota perdida. Similitud: " + porcentaje + "%"
            );
        }

        if (runUsuarioEncontrado != null && !runUsuarioEncontrado.equals(runUsuarioPerdido)) {
            notificacionServices.crearNotificacionCoincidencia(
                runUsuarioEncontrado.longValue(),
                evento.getReporteEncontradoId(),
                saved.getId(),
                "Tu reporte tiene una coincidencia potencial",
                "El animal encontrado que reportaste podria coincidir con una mascota perdida. Similitud: " + porcentaje + "%"
            );
        }

        log.info("Coincidencia {} procesada. runPerdido={}, runEncontrado={}, score={}%",
            evento.getCoincidenciaId(), runUsuarioPerdido, runUsuarioEncontrado, porcentaje);
    }

    public Coincidencia confirmarCoincidencia(Long id) {
        Coincidencia coincidencia = buscarPorId(id);
        if (coincidencia.getEstado() != EstadoCoincidencia.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar coincidencias en estado PENDIENTE");
        }
        coincidencia.setEstado(EstadoCoincidencia.CONFIRMADA);
        coincidencia.setFechaResolucion(LocalDateTime.now());
        return coincidenciaRepository.save(coincidencia);
    }

    public Coincidencia rechazarCoincidencia(Long id) {
        Coincidencia coincidencia = buscarPorId(id);
        if (coincidencia.getEstado() != EstadoCoincidencia.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar coincidencias en estado PENDIENTE");
        }
        coincidencia.setEstado(EstadoCoincidencia.DESCARTADA);
        coincidencia.setFechaResolucion(LocalDateTime.now());
        return coincidenciaRepository.save(coincidencia);
    }

    public Coincidencia buscarPorId(Long id) {
        return coincidenciaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Coincidencia no encontrada con ID: " + id));
    }

    public List<Coincidencia> buscarPorUsuario(Integer runUsuario) {
        List<Coincidencia> resultado = new ArrayList<>();
        resultado.addAll(coincidenciaRepository.findByRunUsuarioPerdido(runUsuario));
        resultado.addAll(coincidenciaRepository.findByRunUsuarioEncontrado(runUsuario));
        return resultado;
    }

    private ReporteDetalleDTO obtenerReporte(Long reporteId) {
        return restTemplate.getForObject(reportesUrl + "/petly/reportes/" + reporteId, ReporteDetalleDTO.class);
    }
}
