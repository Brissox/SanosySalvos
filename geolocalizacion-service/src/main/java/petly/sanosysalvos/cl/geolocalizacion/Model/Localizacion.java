package petly.sanosysalvos.cl.geolocalizacion.Model;


import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "geolocalizacion")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor


public class Localizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_reporte", nullable = false)
    private Long idReporte;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point ubicacion;
    }