package petly.sanosysalvos.cl.geolocalizacion.Model;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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