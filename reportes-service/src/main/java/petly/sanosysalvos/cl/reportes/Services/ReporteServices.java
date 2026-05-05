package petly.sanosysalvos.cl.reportes.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.reportes.Client.GeoClient;
import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.GeoRequest;
import petly.sanosysalvos.cl.reportes.DTO.GeoResponse;
import petly.sanosysalvos.cl.reportes.DTO.ReporteGeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteRequest;
import petly.sanosysalvos.cl.reportes.Model.Especie;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.Sexo;
import petly.sanosysalvos.cl.reportes.Model.Tamanio;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;


@Service
@Transactional
public class ReporteServices {

    
    private final ReporteRepository reporterepository;
    private final GeoClient geoClient;

    public ReporteServices(ReporteRepository reporterepository, GeoClient geoClient) {
        this.reporterepository = reporterepository;
        this.geoClient = geoClient;
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

        //Eliminar reporte + geo
        public void eliminarGeo(Long id) {

        Reporte reporte = reporterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        Long localizacionId = reporte.getLocalizacionId();

        reporterepository.delete(reporte);

        if (localizacionId != null) {
            geoClient.eliminarDTO(localizacionId);
        }
        }

        //Crear
        public Reporte crear(ReporteRequest dto) {

        // 1. Crear geo
        GeoRequest geoReq = new GeoRequest();
        geoReq.setLatitud(dto.getLatitud());
        geoReq.setLongitud(dto.getLongitud());

        GeoResponse geoRes = geoClient.crear(geoReq);

        // 2. Crear reporte
        Reporte r = new Reporte();
        r.setLocalizacionId(geoRes.getId());
        r.setDescripcion(dto.getDescripcion());
        r.setContacto(dto.getContacto());
        r.setFechaReporte(dto.getFechaReporte());
        r.setImagenUrl(dto.getImagenUrl());
        r.setEstadoMascota(dto.getEstadoMascota());
        r.setTipoReporte(TipoReporte.valueOf(dto.getTipoReporte()));
        r.setEspecie(Especie.valueOf(dto.getEspecie()));
        r.setRaza(dto.getRaza());
        r.setColorPrincipal(dto.getColorPrincipal());
        r.setTamanio(Tamanio.valueOf(dto.getTamanio()));
        r.setSexo(Sexo.valueOf(dto.getSexo()));
        r.setEdadAproximada(dto.getEdadAproximada());
        r.setEstadoReporte(EstadoReporte.ACTIVO);

        return reporterepository.save(r);
        }

        //Listar con datos de Localización
        public List<ReporteGeoDTO> listar() {

        List<Reporte> reportes = reporterepository.findAll();

        return reportes.stream().map((Reporte r) -> {

            GeoDTO geo = geoClient.obtener(r.getLocalizacionId());

            ReporteGeoDTO dto = new ReporteGeoDTO();

            dto.setId(r.getIdreporte());
            dto.setTipoReporte(r.getTipoReporte().name());
            dto.setEstadoReporte(r.getEstadoReporte().name());
            dto.setEstadoMascota(r.getEstadoMascota());
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
        public List<Reporte> filtrarPorTipo(TipoReporte tipoReporte) {
        return reporterepository.findByTipoReporte(tipoReporte);
        }

        // LISTAR Reportes por Estado
        public List<Reporte> filtrarPorEstado(EstadoReporte estadoReporte) {
        return reporterepository.findByEstadoReporte(estadoReporte);
        }

       
  
}
