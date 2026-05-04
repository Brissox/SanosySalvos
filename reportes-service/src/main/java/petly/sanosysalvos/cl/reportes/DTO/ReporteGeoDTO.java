package petly.sanosysalvos.cl.reportes.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor



public class ReporteGeoDTO {

    private Long id;
    private String tipo_reporte;
    private String estado_mascota;
    private LocalDateTime fecha_reporte;
    private String descripcion;
    private String contacto;
    private String imagen_url;
    private String Especie;
    private String raza;
    private String color_principal;
    private String tamanio;
    private String sexo;
    private String edad_aproximada;
    
    private Double latitud;
    private Double longitud;

    private String estado_reporte;
}
