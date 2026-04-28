package petly.sanosysalvos.cl.geolocalizacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor


public class GeoDTO {
    private Long idReporte;
    private Double latitud;
    private Double longitud;
}