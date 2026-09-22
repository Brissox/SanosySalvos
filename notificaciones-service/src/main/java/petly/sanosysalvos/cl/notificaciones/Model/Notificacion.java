package petly.sanosysalvos.cl.notificaciones.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICACION")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICACION")
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 100)
    private String titulo;

    @Column(name = "MENSAJE", nullable = false, length = 300)
    private String mensaje;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "LEIDA", nullable = false)
    private boolean leida;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @Column(name = "ID_REPORTE")
    private Long idReporte;

    @Column(name = "ID_COINCIDENCIA")
    private Long idCoincidencia;

    @Column(name = "ID_REPORTE_COINCIDENCIA")
    private Long idReporteCoincidencia;
}
