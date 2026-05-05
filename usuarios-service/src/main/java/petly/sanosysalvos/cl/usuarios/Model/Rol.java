package petly.sanosysalvos.cl.usuarios.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Rol {
    @Id
    @Column(name = "id_rol", nullable = false, unique = true)
    private Integer idRol;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombreRol;
}
