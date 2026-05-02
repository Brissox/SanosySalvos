package petly.sanosysalvos.cl.reportes.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class GeoRequest {
    private Double latitud;
    private Double longitud;
    
}
