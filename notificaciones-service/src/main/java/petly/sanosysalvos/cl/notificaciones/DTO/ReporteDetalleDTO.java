package petly.sanosysalvos.cl.notificaciones.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteDetalleDTO {

    private Long reporteId;
    private Integer runUsuario;
    private String tipoReporte;
    private String especie;
    private String raza;
    private String contacto;
}
