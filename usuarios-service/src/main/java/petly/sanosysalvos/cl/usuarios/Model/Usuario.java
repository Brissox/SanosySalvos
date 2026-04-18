package petly.sanosysalvos.cl.usuarios.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="USUARIO")
@Data
@AllArgsConstructor
@NoArgsConstructor


public class Usuario {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "ID_USUARIO")
    private long id_usuario;


    @Column(name= "NOMBRE",nullable= false , length = 30)
    private String nombre;

    @Column(name= "APELLIDO_PATERNO",nullable= false , length = 30)
    private String apellido_paterno;

    @Column(name= "APELLIDO_MATERNO",nullable= true , length = 30)
    private String apellido_materno;

    @Column(name = "TELEFONO",nullable= true , length = 9)
    private Long telefono;

    @Column(name = "DIRECCION",nullable= true , length = 50)
    private String direccion;


    @Column(name = "CORREO",nullable= false , length = 100)
    private String correo;

    @Column(name = "CONTRASENA",nullable= false , length = 20)
    private String contrasena;

    @Column(name = "RUN",nullable= false , length = 9)
    private int run;

    @Column(name = "DV",nullable= false , length = 1)
    private String dv;

    
}
