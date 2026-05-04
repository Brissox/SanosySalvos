package petly.sanosysalvos.cl.mascotas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MASCOTA")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Mascota {

    @Id
    @Column(name = "CHIP", unique = true, length = 50)
    private String chip;

    @Column(name = "RUNUSUARIO", length = 9)
    private Integer runUsuario;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "SEXO", nullable = false, length = 10)
    private String sexo;

    @Column(name = "RAZA", nullable = false, length = 50)
    private String raza;

    @Column(name = "COLOR", nullable = false, length = 50)
    private String color;

    @Column(name = "EDAD")
    private Integer edad;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMascota tipo;

    @Column(name = "OTROTIPO", length = 100)
    private String otroTipo;

    @Column(name = "DESCRIPCION", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "IMAGEN_URL")
    private String imagenUrl;

}