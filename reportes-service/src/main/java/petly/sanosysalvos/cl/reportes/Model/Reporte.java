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
    private long idreporte;

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

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "CONTACTO", length = 100)
    private String contacto;

    @Column(name = "IMAGEN_URL", length = 255)
    private String imagen_url;

    @Column(name = "localizacion_id")
    private Long localizacionId;

    @Column(name = "ESPECIE", length = 50)
    @Enumerated(EnumType.STRING)
    private Especie especie;

    @Column(name = "RAZA", length = 100)
    private String raza;

    @Column(name = "COLOR_PRINCIPAL", length = 50)
    private String color_principal;

    @Column(name = "TAMANIO", length = 20)
    @Enumerated(EnumType.STRING)
    private Tamanio tamanio;

    @Column(name = "SEXO", length = 10)
    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Column(name = "EDAD_APROXIMADA", length = 30)
    private String edad_aproximada;

}
