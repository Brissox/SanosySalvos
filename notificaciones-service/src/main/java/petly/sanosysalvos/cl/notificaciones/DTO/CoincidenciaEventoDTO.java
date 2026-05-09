package petly.sanosysalvos.cl.notificaciones.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CoincidenciaEventoDTO {

    @JsonProperty("coincidencia_id")
    private Long coincidenciaId;

    @JsonProperty("reporte_perdido_id")
    private Long reportePerdidoId;

    @JsonProperty("reporte_encontrado_id")
    private Long reporteEncontradoId;

    private Double score;
}
