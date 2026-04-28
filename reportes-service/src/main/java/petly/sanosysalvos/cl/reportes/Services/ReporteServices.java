package petly.sanosysalvos.cl.reportes.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.reportes.Client.GeoClient;
import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteGeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteRequest;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;


@Service
@Transactional

public class ReporteServices {
     @Autowired

    private final ReporteRepository reporterepository;

    public List<Reporte> BuscarTodoReporte(){
        return reporterepository.findAll();
    }

    public Reporte BuscarUnReporte(Long id_reporte){
        return reporterepository.findById(id_reporte).get();

    }

    public Reporte GuardarReporte(Reporte Reporte){
        return reporterepository.save(Reporte);

    }

    public void EliminarReporte(Long id_reporte){
        reporterepository.deleteById(id_reporte);
    }

    /*Nuevo*/

    private final GeoClient geoClient;

    public ReporteServices(GeoClient geoClient, ReporteRepository reporterepository) {
        this.geoClient = geoClient;
        this.reporterepository = reporterepository;
    }

    public ReporteGeoDTO obtenerReporteConUbicacion(Long id) {

        Reporte reporte = reporterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe"));

        GeoDTO geo = geoClient.obtenerUbicacion(id);

        return new ReporteGeoDTO(
                reporte.getId_reporte(),
                reporte.getComuna(),
                reporte.getDescripcion(),
                geo.getLatitud(),
                geo.getLongitud()
        );
    }

     public Reporte crearReporte(ReporteRequest request) {

        //Crear y guardar reporte primero
        Reporte reporte = new Reporte();
        reporte.setDescripcion(request.getDescripcion());
        reporte.setTipo_reporte(request.getTipo()); // perdido / encontrado
        reporte.setFecha_reporte(LocalDateTime.now());

        Reporte savedReporte = reporterepository.save(reporte);

        // Crear DTO de geolocalización
        GeoDTO geo = new GeoDTO();
        geo.setIdReporte(savedReporte.getId_reporte());
        geo.setLatitud(request.getLatitud());
        geo.setLongitud(request.getLongitud());

        //Llamar microservicio de geolocalización
        try {
            geoClient.guardarUbicacion(geo);
        } catch (Exception e) {
            //evitar que falle todo el sistema
            System.out.println("Error guardando ubicación: " + e.getMessage());

            
        }

        //devolver reporte creado
        return savedReporte;
    }
}

