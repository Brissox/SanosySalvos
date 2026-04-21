package petly.sanosysalvos.cl.reportes.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;


@Service
@Transactional

public class ReporteServices {
     @Autowired

    private ReporteRepository reporterepository;

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

}
