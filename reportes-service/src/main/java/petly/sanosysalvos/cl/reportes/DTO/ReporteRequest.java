package petly.sanosysalvos.cl.reportes.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoAllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ReporteRequest {

    private String titulo;
    private String descripcion;
    private String tipo;

    private Double latitud;
    private Double longitud;

}