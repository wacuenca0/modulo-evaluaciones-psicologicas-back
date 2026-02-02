package ec.mil.dsndft.servicio_gestion.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "atenciones_psicologicas")
public class AtencionPsicologica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_atenciones")
    @SequenceGenerator(name = "seq_atenciones", sequenceName = "seq_atenciones", allocationSize = 1)
    private Long id;

    // ========== RELACIONES CON ENTIDADES EXISTENTES ==========
    
    // Paciente (PersonalMilitar)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_militar_id", nullable = false)
    private PersonalMilitar personalMilitar;

    // Psicólogo asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id", nullable = false)
    private Psicologo psicologo;

    // Relación con FichaPsicologica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_psicologica_id")
    private FichaPsicologica fichaPsicologica;

    // ========== INFORMACIÓN BÁSICA DE LA ATENCIÓN ==========
    
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;
    
    @Column(name = "hora_inicio", nullable = false, length = 5)
    private String horaInicio; // Formato "08:30"
    
    @Column(name = "hora_fin", nullable = false, length = 5)
    private String horaFin;
    
    @Column(name = "numero_sesion")
    private Integer numeroSesion; // 1 = Primera vez, 2 = Segunda, etc.

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String tipoAtencion = "PRESENCIAL"; // PRESENCIAL, TELEFONICA, VIRTUAL

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String tipoConsulta = "PRIMERA_VEZ"; // PRIMERA_VEZ, SEGUIMIENTO, URGENCIA

    // ========== DATOS CLÍNICOS ==========
    
    @Column(name = "motivo_consulta", nullable = false, length = 500)
    private String motivoConsulta;
    
    @Column(name = "anamnesis", columnDefinition = "CLOB")
    private String anamnesis; // Historia clínica
    
    @Column(name = "examen_mental", columnDefinition = "CLOB")
    private String examenMental; // Estado mental durante la sesión
    
    @Column(name = "impresion_diagnostica", columnDefinition = "CLOB")
    private String impresionDiagnostica; // Lo que observa el psicólogo
    
    @Column(name = "plan_intervencion", columnDefinition = "CLOB")
    private String planIntervencion; // Qué se va a hacer
    
    @Column(name = "recomendaciones", columnDefinition = "CLOB")
    private String recomendaciones;
    
    @Column(name = "derivacion", length = 200)
    private String derivacion; // Si deriva a otro especialista

    // ========== DIAGNÓSTICOS CIE-10 ==========
    
    @ManyToMany
    @JoinTable(
        name = "atencion_diagnosticos_cie10",
        joinColumns = @JoinColumn(name = "atencion_id"),
        inverseJoinColumns = @JoinColumn(name = "diagnostico_id")
    )
    private List<CatalogoDiagnosticoCie10> diagnosticos;

    // ========== PROXIMA CITA Y SEGUIMIENTO ==========
    
    @Column(name = "proxima_cita")
    private LocalDate proximaCita;
    
    @Column(name = "observaciones_proxima_cita", length = 300)
    private String observacionesProximaCita;

    // ========== ESTADO Y AUDITORÍA ==========
    
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "PROGRAMADA"; // PROGRAMADA, EN_CURSO, FINALIZADA, CANCELADA, NO_ASISTIO
    
    @Column(name = "razon_cancelacion", length = 200)
    private String razonCancelacion;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ========== MÉTODOS AUXILIARES ==========
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Inicializar valores por defecto si son nulos
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.estado == null) {
            this.estado = "PROGRAMADA";
        }
        if (this.tipoAtencion == null) {
            this.tipoAtencion = "PRESENCIAL";
        }
        if (this.tipoConsulta == null) {
            this.tipoConsulta = "PRIMERA_VEZ";
        }
        
        calcularNumeroSesion();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Método para calcular número de sesión automáticamente
    public void calcularNumeroSesion() {
        if (this.numeroSesion == null && this.personalMilitar != null && this.tipoConsulta != null) {
            if ("PRIMERA_VEZ".equals(this.tipoConsulta)) {
                this.numeroSesion = 1;
            }
            // Para seguimientos, se podría consultar cuántas atenciones previas tiene
        }
    }

    // Método para finalizar la atención
    public void finalizarAtencion() {
        this.estado = "FINALIZADA";
        this.updatedAt = LocalDateTime.now();
    }

    // Método para cancelar atención
    public void cancelarAtencion(String razon) {
        this.estado = "CANCELADA";
        this.razonCancelacion = razon;
        this.updatedAt = LocalDateTime.now();
    }

    // Método para registrar no asistencia
    public void registrarNoAsistencia() {
        this.estado = "NO_ASISTIO";
        this.updatedAt = LocalDateTime.now();
    }

    // Método para verificar si la atención está activa
    public boolean estaActiva() {
        return this.activo && 
               !"CANCELADA".equals(this.estado) && 
               !"NO_ASISTIO".equals(this.estado);
    }

    // Método para obtener duración de la sesión en minutos
    public Integer obtenerDuracionMinutos() {
        try {
            String[] inicioParts = this.horaInicio.split(":");
            String[] finParts = this.horaFin.split(":");
            
            int inicioHoras = Integer.parseInt(inicioParts[0]);
            int inicioMinutos = Integer.parseInt(inicioParts[1]);
            int finHoras = Integer.parseInt(finParts[0]);
            int finMinutos = Integer.parseInt(finParts[1]);
            
            int totalMinutosInicio = inicioHoras * 60 + inicioMinutos;
            int totalMinutosFin = finHoras * 60 + finMinutos;
            
            return totalMinutosFin - totalMinutosInicio;
        } catch (Exception e) {
            return null;
        }
    }
    
    public FichaPsicologica getFichaPsicologica() {
        return fichaPsicologica;
    }

    public void setFichaPsicologica(FichaPsicologica fichaPsicologica) {
        this.fichaPsicologica = fichaPsicologica;
    }
}