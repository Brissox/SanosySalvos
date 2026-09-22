package petly.sanosysalvos.cl.notificaciones.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteVencimientoDTO {
    private Long reporteId;
    private Integer runUsuario;
    private String tipoReporte;
    private LocalDateTime fechaLimite;
}
