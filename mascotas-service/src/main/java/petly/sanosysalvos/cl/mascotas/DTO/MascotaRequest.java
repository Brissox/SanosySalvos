package petly.sanosysalvos.cl.mascotas.DTO;

import java.time.LocalDate;

import lombok.Data;
import petly.sanosysalvos.cl.mascotas.Model.TipoMascota;

@Data
public class MascotaRequest {

    private String nombre;
    private String numeroMicrochip;
    private String especie;
    private String sexo;
    private String raza;
    private String color;
    private LocalDate fechaNacimiento;
    private String estadoReproductivo;

    private TipoMascota tipo;
    private String ubicacion;
}