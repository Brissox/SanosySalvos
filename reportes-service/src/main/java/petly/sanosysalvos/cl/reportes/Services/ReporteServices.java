package petly.sanosysalvos.cl.reportes.Services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.reportes.Client.GeoClientResiliente;
import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.GeoRequest;
import petly.sanosysalvos.cl.reportes.DTO.GeoResponse;
import petly.sanosysalvos.cl.reportes.DTO.ReporteEventoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteGeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteRequest;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEventoPublisher;
import petly.sanosysalvos.cl.reportes.Model.Especie;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.Sexo;
import petly.sanosysalvos.cl.reportes.Model.Tamanio;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;
import petly.sanosysalvos.cl.reportes.Services.OracleStorageService;

@Service
@Transactional
public class ReporteServices {

    private final ReporteRepository reporterepository;
    private final GeoClientResiliente geoClient;
    private final OracleStorageService oracleStorageService;
    private final ReporteEventoPublisher reporteEventoPublisher;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    public ReporteServices(ReporteRepository reporterepository, GeoClientResiliente geoClient,
            OracleStorageService oracleStorageService, ReporteEventoPublisher reporteEventoPublisher) {
        this.reporterepository = reporterepository;
        this.geoClient = geoClient;
        this.oracleStorageService = oracleStorageService;
        this.reporteEventoPublisher = reporteEventoPublisher;
    }

    // LISTAR Reportes
    public List<Reporte> buscarTodos() {
        return reporterepository.findAll();
    }

    // BUSCAR POR ID (con control correcto)
    public Reporte buscarPorId(Long id) {
        return reporterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    // ELIMINAR
    public void eliminar(Long id) {
        if (!reporterepository.existsById(id)) {
            throw new RuntimeException("No existe el reporte");
        }
        reporterepository.deleteById(id);
    }

    // Cerrar reporte (soft delete): libera recursos pero mantiene el registro
    public void eliminarGeo(Long id) {

        Reporte reporte = reporterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        Long localizacionId = reporte.getLocalizacionId();
        String imagenUrl = reporte.getImagenUrl();

        reporte.setEstadoReporte(EstadoReporte.CERRADO);
        reporterepository.save(reporte);

        if (localizacionId != null) {
            geoClient.eliminarDTO(localizacionId);
        }

        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            oracleStorageService.eliminarImagen(imagenUrl);
        }

        reporteEventoPublisher.publicarReporteCerrado(id);
    }

    // Crear
    public Reporte crear(ReporteRequest dto, MultipartFile imagen, Integer run) throws IOException {

        GeoRequest geoReq = new GeoRequest();
        geoReq.setLatitud(dto.getLatitud());
        geoReq.setLongitud(dto.getLongitud());

        // Ejecutar llamada a geo y subida de imagen en paralelo (virtual threads)
        CompletableFuture<GeoResponse> geoFuture = CompletableFuture
                .supplyAsync(() -> geoClient.crear(geoReq), executor);

        CompletableFuture<String> imagenFuture = (imagen != null && !imagen.isEmpty())
                ? CompletableFuture.supplyAsync(() -> {
                    try {
                        return oracleStorageService.subirImagen(imagen);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executor)
                : CompletableFuture.completedFuture(null);

        try {
            CompletableFuture.allOf(geoFuture, imagenFuture).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException ioEx) throw ioEx;
            if (cause instanceof RuntimeException rtEx) throw rtEx;
            throw new RuntimeException(cause);
        }

        GeoResponse geoRes = geoFuture.join();
        String imagenUrl = imagenFuture.join();

        // Crear reporte
        Reporte r = new Reporte();

        r.setLocalizacionId(geoRes.getId());
        r.setDescripcion(dto.getDescripcion());
        r.setContacto(dto.getContacto());
        r.setFechaReporte(LocalDateTime.now());
        r.setImagenUrl(imagenUrl);
        r.setTipoReporte(TipoReporte.valueOf(dto.getTipoReporte()));

        if ("OTRO".equalsIgnoreCase(dto.getEspecie())) {
            r.setEspecie(Especie.OTRO);

            if (dto.getOtraEspecie() == null || dto.getOtraEspecie().isBlank()) {
                throw new RuntimeException("Debe especificar la otra especie");
            }

            r.setOtraEspecie(dto.getOtraEspecie());
        } else {
            r.setEspecie(Especie.valueOf(dto.getEspecie()));
            r.setOtraEspecie(null);
        }

        r.setRaza(dto.getRaza());
        r.setColorPrincipal(dto.getColorPrincipal());
        r.setTamanio(Tamanio.valueOf(dto.getTamanio()));
        r.setSexo(Sexo.valueOf(dto.getSexo()));
        r.setEdadAproximada(dto.getEdadAproximada());
        r.setEstadoReporte(EstadoReporte.ACTIVO);
        r.setRunUsuario(run);
        r.setFechaLimite(calcularFechaLimite(TipoReporte.valueOf(dto.getTipoReporte())));

        Reporte saved = reporterepository.save(r);

        ReporteEventoDTO evento = ReporteEventoDTO.builder()
                .reporteId(saved.getIdreporte())
                .tipoReporte(saved.getTipoReporte().name())
                .especie(saved.getEspecie().name())
                .raza(saved.getRaza())
                .colorPrincipal(saved.getColorPrincipal())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .fechaReporte(saved.getFechaReporte())
                .imagenUrl(saved.getImagenUrl())
                .descripcion(saved.getDescripcion())
                .tamanio(saved.getTamanio() != null ? saved.getTamanio().name() : null)
                .sexo(saved.getSexo() != null ? saved.getSexo().name() : null)
                .edadAproximada(saved.getEdadAproximada())
                .contacto(saved.getContacto())
                .runUsuario(saved.getRunUsuario())
                .otraEspecie(saved.getOtraEspecie())
                .estadoReporte(saved.getEstadoReporte().name())
                .build();

        reporteEventoPublisher.publicarReporteNuevo(evento);

        return saved;
    }

    // Listar con datos de Localización
    public List<ReporteGeoDTO> listar() {

        List<Reporte> reportes = reporterepository.findAll();

        return reportes.stream().map((Reporte r) -> {

            GeoDTO geo = geoClient.obtener(r.getLocalizacionId());

            ReporteGeoDTO dto = new ReporteGeoDTO();

            dto.setId(r.getIdreporte());
            dto.setTipoReporte(r.getTipoReporte().name());
            dto.setEstadoReporte(r.getEstadoReporte().name());
            dto.setFechaReporte(r.getFechaReporte());
            dto.setDescripcion(r.getDescripcion());
            dto.setContacto(r.getContacto());
            dto.setImagenUrl(r.getImagenUrl());
            dto.setLatitud(geo.getLatitud());
            dto.setLongitud(geo.getLongitud());
            dto.setEspecie(r.getEspecie().name());
            dto.setRaza(r.getRaza());
            dto.setColorPrincipal(r.getColorPrincipal());
            dto.setTamanio(r.getTamanio().name());
            dto.setSexo(r.getSexo().name());
            dto.setEdadAproximada(r.getEdadAproximada());

            return dto;
        })
                .collect(Collectors.toList());
    }

    // LISTAR Reportes por Tipo
    public List<ReporteGeoDTO> filtrarPorTipo(TipoReporte tipoReporte) {
        List<Reporte> reportes = reporterepository.findByTipoReporte(tipoReporte);
        return reportes.stream().map((Reporte r) -> {

        GeoDTO geo = geoClient.obtener(r.getLocalizacionId());

        ReporteGeoDTO dto = new ReporteGeoDTO();

        dto.setId(r.getIdreporte());
        dto.setTipoReporte(r.getTipoReporte().name());
        dto.setEstadoReporte(r.getEstadoReporte().name());
        dto.setFechaReporte(r.getFechaReporte());
        dto.setDescripcion(r.getDescripcion());
        dto.setContacto(r.getContacto());
        dto.setImagenUrl(r.getImagenUrl());
        dto.setLatitud(geo.getLatitud());
        dto.setLongitud(geo.getLongitud());
        dto.setEspecie(r.getEspecie().name());
        dto.setRaza(r.getRaza());
        dto.setColorPrincipal(r.getColorPrincipal());
        dto.setTamanio(r.getTamanio().name());
        dto.setSexo(r.getSexo().name());
        dto.setEdadAproximada(r.getEdadAproximada());

        return dto;

    }).collect(Collectors.toList());
}

    // LISTAR Reportes por Estado
    public List<ReporteGeoDTO> filtrarPorEstado(EstadoReporte estadoReporte) {
        List<Reporte> reportes = reporterepository.findByEstadoReporte(estadoReporte);
        return reportes.stream().map((Reporte r) -> {

        GeoDTO geo = geoClient.obtener(r.getLocalizacionId());

        ReporteGeoDTO dto = new ReporteGeoDTO();

        dto.setId(r.getIdreporte());
        dto.setTipoReporte(r.getTipoReporte().name());
        dto.setEstadoReporte(r.getEstadoReporte().name());
        dto.setFechaReporte(r.getFechaReporte());
        dto.setDescripcion(r.getDescripcion());
        dto.setContacto(r.getContacto());
        dto.setImagenUrl(r.getImagenUrl());
        dto.setLatitud(geo.getLatitud());
        dto.setLongitud(geo.getLongitud());
        dto.setEspecie(r.getEspecie().name());
        dto.setRaza(r.getRaza());
        dto.setColorPrincipal(r.getColorPrincipal());
        dto.setTamanio(r.getTamanio().name());
        dto.setSexo(r.getSexo().name());
        dto.setEdadAproximada(r.getEdadAproximada());

        return dto;

    }).collect(Collectors.toList());
}


    private LocalDateTime calcularFechaLimite(TipoReporte tipo) {
        int dias = switch (tipo) {
            case AVISTAMIENTO -> 14;
            case ENCONTRADA -> 30;
            case PERDIDA -> 60;
        };
        return LocalDateTime.now().plusDays(dias);
    }

    public void renovarReporte(Long id) {
        Reporte reporte = reporterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado: " + id));
        if (reporte.getEstadoReporte() == EstadoReporte.CERRADO) {
            throw new RuntimeException("No se puede renovar un reporte cerrado");
        }
        reporte.setFechaLimite(calcularFechaLimite(reporte.getTipoReporte()));
        reporterepository.save(reporte);
    }

    public void actualizarEstado(Long id, EstadoReporte nuevoEstado) {
        Reporte reporte = reporterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado: " + id));
        reporte.setEstadoReporte(nuevoEstado);
        reporterepository.save(reporte);

        if (nuevoEstado == EstadoReporte.RESUELTO) {
            Long localizacionId = reporte.getLocalizacionId();
            String imagenUrl = reporte.getImagenUrl();
            if (localizacionId != null) {
                geoClient.eliminarDTO(localizacionId);
            }
            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                oracleStorageService.eliminarImagen(imagenUrl);
            }
            reporteEventoPublisher.publicarReporteCerrado(id);
        }
    }

    public List<ReporteGeoDTO> buscarPorRunUsuario(Integer runUsuario) {

    List<Reporte> reportes = reporterepository.findByRunUsuario(runUsuario);

    return reportes.stream().map((Reporte r) -> {

        GeoDTO geo = geoClient.obtener(r.getLocalizacionId());

        ReporteGeoDTO dto = new ReporteGeoDTO();

        dto.setId(r.getIdreporte());
        dto.setTipoReporte(r.getTipoReporte().name());
        dto.setEstadoReporte(r.getEstadoReporte().name());
        dto.setFechaReporte(r.getFechaReporte());
        dto.setDescripcion(r.getDescripcion());
        dto.setContacto(r.getContacto());
        dto.setImagenUrl(r.getImagenUrl());
        dto.setLatitud(geo.getLatitud());
        dto.setLongitud(geo.getLongitud());
        dto.setEspecie(r.getEspecie().name());
        dto.setRaza(r.getRaza());
        dto.setColorPrincipal(r.getColorPrincipal());
        dto.setTamanio(r.getTamanio().name());
        dto.setSexo(r.getSexo().name());
        dto.setEdadAproximada(r.getEdadAproximada());

        return dto;

    }).collect(Collectors.toList());
}
}
