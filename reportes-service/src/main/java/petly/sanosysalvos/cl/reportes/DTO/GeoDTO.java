package petly.sanosysalvos.cl.reportes.DTO;

import lombok.Data;

@Data
public class GeoDTO {
    private Long idReporte;
    private Double latitud;
    private Double longitud;
}