package ec.mil.dsndft.servicio_gestion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "atencion_psicologica_historial")
public class AtencionPsicologicaHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_atencion_historial")
    @SequenceGenerator(name = "seq_atencion_historial", sequenceName = "seq_atencion_historial", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atencion_id", nullable = false)
    private AtencionPsicologica atencion;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "razon_cambio", length = 200)
    private String razonCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id", nullable = false)
    private Psicologo psicologo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;
}
