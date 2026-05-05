package petly.sanosysalvos.cl.reportes.Controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petly.sanosysalvos.cl.reportes.DTO.ReporteGeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.ReporteRequest;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;
import petly.sanosysalvos.cl.reportes.Services.ReporteServices;



@RestController
@RequestMapping("/petly/reportes")
public class ReporteController {

      private final ReporteServices reporteservice;

    //Inyección por constructor
    public ReporteController(ReporteServices reporteservice) {
        this.reporteservice = reporteservice;
    }

    //LISTAR (si está vacío → devuelve [])
    @GetMapping
    public ResponseEntity<List<ReporteGeoDTO>> listar() {
        return ResponseEntity.ok(reporteservice.listar());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Reporte>> buscarTodos() {
        return ResponseEntity.ok(reporteservice.buscarTodos());
    }

    //BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteservice.buscarPorId(id));
    }

    // CREAR (usa DTO correcto)
    @PostMapping
    public ResponseEntity<Reporte> crear(@RequestBody ReporteRequest request) {
        return ResponseEntity.ok(reporteservice.crear(request));
    }

    // ELIMINAR 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reporteservice.eliminarGeo(id);
        return ResponseEntity.noContent().build();
    }

    // FILTRAR POR TIPO
    @GetMapping("/filtrar/tipo")
    public ResponseEntity<List<Reporte>> filtrarPorTipo(
            @RequestParam TipoReporte tipoReporte) {

        return ResponseEntity.ok(
                reporteservice.filtrarPorTipo(tipoReporte)
        );
    }

    // FILTRAR POR ESTADO

    @GetMapping("/filtrar/estado")
    public ResponseEntity<List<Reporte>> filtrarPorEstado(
            @RequestParam EstadoReporte estadoReporte) {

        return ResponseEntity.ok(
                reporteservice.filtrarPorEstado(estadoReporte)
        );
    }
}