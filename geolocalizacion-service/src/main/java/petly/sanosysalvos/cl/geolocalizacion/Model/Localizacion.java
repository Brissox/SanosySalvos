package petly.sanosysalvos.cl.geolocalizacion.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "geolocalizacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Localizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
}
