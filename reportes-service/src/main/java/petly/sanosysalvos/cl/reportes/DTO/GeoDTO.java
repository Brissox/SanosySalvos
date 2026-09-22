package petly.sanosysalvos.cl.reportes.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class GeoDTO {
    private Long idlocalizacion;
    private Double latitud;
    private Double longitud;
}