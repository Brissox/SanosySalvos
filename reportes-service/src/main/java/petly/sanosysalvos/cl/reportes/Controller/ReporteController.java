package petly.sanosysalvos.cl.reportes.Controller;

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

import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Services.ReporteServices;



@RestController
@RequestMapping("/petly/reportes")
public class ReporteController {

    @Autowired
    private ReporteServices reporteservice;

    @GetMapping
    public ResponseEntity<?> ListarReportes() {
        List<Reporte> reportes = reporteservice.BuscarTodoReporte();
        if (reportes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentran reportes");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(reportes);
        }
    }

    @GetMapping("/{id_reporte}")
    public ResponseEntity<?> BuscarUnReportePorId(@PathVariable Long id_reporte) {
        try{
            Reporte reporte = reporteservice.BuscarUnReporte(id_reporte);
            return ResponseEntity.status(HttpStatus.OK).body(reporte);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se encuentra el reporte");
        }

    }
    
     @PostMapping
    public ResponseEntity<?> GuardarReporte(@RequestBody Reporte reporteGuardar) {
        try{
            Reporte reporteRegistrar = reporteservice.GuardarReporte(reporteGuardar);
            return ResponseEntity.ok(reporteRegistrar);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(" no se puede registrar el reporte ");
        }
    }

    @DeleteMapping("/{id_reporte}")
    public ResponseEntity<?> EliminarReporte(@PathVariable Long id_reporte) {
        try{
            Reporte reporteBuscado = reporteservice.BuscarUnReporte(id_reporte);
            reporteservice.EliminarReporte(id_reporte);
            return ResponseEntity.ok(reporteBuscado);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede eliminar el reporte ");
        }
    }

    @PutMapping("/{id_reporte}")
    public ResponseEntity<?> ActualizarReporte(@PathVariable Long id_reporte, @RequestBody Reporte reporteActualizar) {
        try{
            Reporte reporteActualizado = reporteservice.BuscarUnReporte(id_reporte);

            reporteActualizado.setTipo_reporte(reporteActualizar.getTipo_reporte());
            reporteActualizado.setEstado_reporte(reporteActualizar.getEstado_reporte());
            reporteActualizado.setEstado_mascota(reporteActualizar.getEstado_mascota());
            reporteActualizado.setFecha_reporte(reporteActualizar.getFecha_reporte());
            reporteActualizado.setLatitud(reporteActualizar.getLatitud());
            reporteActualizado.setLongitud(reporteActualizar.getLongitud());
            reporteActualizado.setComuna(reporteActualizar.getComuna());
            reporteActualizado.setDescripcion(reporteActualizar.getDescripcion());
            reporteActualizado.setContacto(reporteActualizar.getContacto());
            reporteActualizado.setImagen_url(reporteActualizar.getImagen_url());

            reporteservice.GuardarReporte(reporteActualizado);
            return ResponseEntity.ok(reporteActualizado);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no se puede editar el reporte");
        }

    }
}
