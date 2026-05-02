package petly.sanosysalvos.cl.reportes.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ReporteRequest {

    private Double latitud;
    private Double longitud;

    private String tipo_reporte;
    private String estado_mascota;
    private LocalDateTime fecha_reporte;
    private String descripcion;
    private String contacto;
    private String imagen_url;
    private String estado_reporte;
 
}



