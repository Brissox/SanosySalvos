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
@Table(name = "COINCIDENCIA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Coincidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ID que viene del coincidencias-service (idempotency key)
    @Column(name = "COINCIDENCIA_ID_REF", nullable = false, unique = true)
    private Long coincidenciaIdRef;

    @Column(name = "REPORTE_PERDIDO_ID", nullable = false)
    private Long reportePerdidoId;

    @Column(name = "REPORTE_ENCONTRADO_ID", nullable = false)
    private Long reporteEncontradoId;

    @Column(name = "SCORE", nullable = false)
    private Double score;

    @Column(name = "ESTADO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCoincidencia estado;

    @Column(name = "RUN_USUARIO_PERDIDO")
    private Integer runUsuarioPerdido;

    @Column(name = "RUN_USUARIO_ENCONTRADO")
    private Integer runUsuarioEncontrado;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_RESOLUCION")
    private LocalDateTime fechaResolucion;
}
