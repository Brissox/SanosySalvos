package petly.sanosysalvos.cl.reportes.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteRequest {

    private Double latitud;
    private Double longitud;

    private String tipoReporte;
    private String estadoMascota;
    private LocalDateTime fechaReporte;
    private String descripcion;
    private String contacto;
    private String imagenUrl;
    private String estadoReporte;
    private String especie;
    private String raza;
    private String colorPrincipal;
    private String tamanio;
    private String sexo;
    private String edadAproximada;

}



