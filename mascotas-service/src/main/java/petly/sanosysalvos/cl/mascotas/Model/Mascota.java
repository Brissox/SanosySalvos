package petly.sanosysalvos.cl.mascotas.Model;

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
@Table(name="MASCOTA")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Mascota {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MASCOTA")
    private long id_mascota;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "NUMERO_MICROCHIP", unique = true, length = 50)
    private String numero_microchip;

    @Column(name = "ESPECIE", nullable = false, length = 30)
    private String especie;

    @Column(name = "SEXO", nullable = false, length = 10)
    private String sexo;

    @Column(name = "RAZA", length = 50)
    private String raza;

    @Column(name = "COLOR", length = 50)
    private String color;

    @Column(name = "FECHA_NACIMIENTO")
    private LocalDate fecha_nacimiento;

    @Column(name = "ESTADO_REPRODUCTIVO", length = 30)
    private String estado_reproductivo;
    
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = true)
    private Usuario usuario;
    }