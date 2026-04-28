package petly.sanosysalvos.cl.reportes.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter


public class ReporteGeoDTO {
    private Long id;
    private String comuna;
    private String descripcion;
    private Double latitud;
    private Double longitud;

}
