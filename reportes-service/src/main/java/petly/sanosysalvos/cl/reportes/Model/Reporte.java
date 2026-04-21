package petly.sanosysalvos.cl.reportes.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name="REPORTE")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REPORTE")
    private long id_reporte;

    @Column(name = "TIPO_REPORTE", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoReporte tipo_reporte;

    @Column(name = "ESTADO_REPORTE", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoReporte estado_reporte;

    @Column(name = "ESTADO_MASCOTA", length = 50)
    private String estado_mascota;

    @Column(name = "FECHA_REPORTE", nullable = false)
    private LocalDateTime fecha_reporte;

    @Column(name = "LATITUD", nullable = false)
    private Double latitud;

    @Column(name = "LONGITUD", nullable = false)
    private Double longitud;

    @Column(name = "COMUNA", nullable = false, length = 100)
    private String comuna;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "CONTACTO", length = 100)
    private String contacto;

    @Column(name = "IMAGEN_URL", length = 255)
    private String imagen_url;


}
