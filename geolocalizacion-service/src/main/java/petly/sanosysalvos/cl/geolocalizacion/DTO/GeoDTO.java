package petly.sanosysalvos.cl.geolocalizacion.DTO;

import lombok.Data;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor

@Data

public class GeoDTO {
    private Long idReporte;
    private Double latitud;
    private Double longitud;
}