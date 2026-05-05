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

    private String tipoReporte;
    private String estadoMascota;
    private LocalDateTime fechaReporte;
    private String descripcion;
    private String contacto;
    private String imagenUrl;
    private String estadoReporte;
 
}



