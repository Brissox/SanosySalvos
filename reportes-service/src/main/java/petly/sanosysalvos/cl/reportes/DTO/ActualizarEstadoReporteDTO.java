package petly.sanosysalvos.cl.reportes.DTO;

public class ActualizarEstadoReporteDTO {

    private Long reporteId;
    private String nuevoEstado;

    public Long getReporteId() { return reporteId; }
    public void setReporteId(Long reporteId) { this.reporteId = reporteId; }

    public String getNuevoEstado() { return nuevoEstado; }
    public void setNuevoEstado(String nuevoEstado) { this.nuevoEstado = nuevoEstado; }
}
