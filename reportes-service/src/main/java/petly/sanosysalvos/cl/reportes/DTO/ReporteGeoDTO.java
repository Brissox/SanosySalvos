package petly.sanosysalvos.cl.reportes.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor



public class ReporteGeoDTO {

    private Long id;
    private String tipoReporte;
    private LocalDateTime fechaReporte;
    private String descripcion;
    private String contacto;
    private String imagenUrl;
    private String especie;
    private String raza;
    private String colorPrincipal;
    private String tamanio;
    private String sexo;
    private Integer edadAproximada;
    
    private Double latitud;
    private Double longitud;

    private String estadoReporte;
}
