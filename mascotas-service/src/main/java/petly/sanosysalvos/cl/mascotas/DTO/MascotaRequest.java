package petly.sanosysalvos.cl.mascotas.DTO;

import lombok.Data;

@Data
public class MascotaRequest {
    private String chip;
    private String nombre;
    private String sexo;
    private Integer edad;
    private String tipo;
    private String color;
    private String raza;
    private String descripcion;
    private String otroTipo;
}