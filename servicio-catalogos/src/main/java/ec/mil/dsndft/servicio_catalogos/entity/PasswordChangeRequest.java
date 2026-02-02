package ec.mil.dsndft.servicio_catalogos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ec.mil.dsndft.servicio_catalogos.entity.Usuario;

@Entity
@Table(name = "password_change_requests")
public class PasswordChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_change_requests_seq")
    @SequenceGenerator(name = "password_change_requests_seq", sequenceName = "SEQ_PASSWORD_CHANGE_REQUESTS", allocationSize = 1)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private Usuario usuario;

    @Column(name = "username_snapshot", nullable = false, length = 50)
    private String usernameSnapshot;

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDIENTE;

    @Column(length = 500)
    private String motivo;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "processed_by")
    private String processedBy;

    public enum RequestStatus {
        PENDIENTE, APROBADO, RECHAZADO
    }

    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getUsernameSnapshot() { return usernameSnapshot; }
    public void setUsernameSnapshot(String usernameSnapshot) { this.usernameSnapshot = usernameSnapshot; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
}
