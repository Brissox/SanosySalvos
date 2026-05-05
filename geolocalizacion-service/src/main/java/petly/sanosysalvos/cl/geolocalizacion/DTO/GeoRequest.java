package petly.sanosysalvos.cl.geolocalizacion.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class GeoRequest {
    
    private Double latitud;
    private Double longitud;
}