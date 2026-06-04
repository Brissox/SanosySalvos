package petly.sanosysalvos.cl.usuarios.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petly.sanosysalvos.cl.usuarios.Model.Rol;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CrearUsuarioRequest {

    private Integer run;
    private String nombre;
    private String apellido_paterno;
    private String apellido_materno;
    private String telefono;
    private String direccion;
    private String correo;
    private String contrasena;
    private String dv;
    private Rol rol;
}
    

