package petly.sanosysalvos.cl.reportes.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteVencimientoDTO {
    private Long reporteId;
    private Integer runUsuario;
    private String tipoReporte;
    private LocalDateTime fechaLimite;
}
