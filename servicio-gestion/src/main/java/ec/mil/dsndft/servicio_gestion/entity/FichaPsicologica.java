package ec.mil.dsndft.servicio_gestion.entity;

import ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.EstadoFichaEnum;
// import eliminado: TipoEvaluacionEnum
import ec.mil.dsndft.servicio_gestion.model.value.DiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.model.value.ObservacionClinica;
import ec.mil.dsndft.servicio_gestion.model.value.PlanSeguimiento;
import ec.mil.dsndft.servicio_gestion.model.value.TransferenciaInfo;
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisInfancia;
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisNatal;
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisPrenatal;
// NUEVOS VALUE OBJECTS
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisAdolescenciaJuventudAdultez;
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisFamiliar;
import ec.mil.dsndft.servicio_gestion.model.value.ExamenFuncionesPsicologicas;
import ec.mil.dsndft.servicio_gestion.model.value.DiagnosticoRasgosExamenesPsicologicos;
import ec.mil.dsndft.servicio_gestion.model.value.FormulacionEtiopatogenicaPronostico;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "fichas_psicologicas",
    indexes = {
        @Index(name = "ix_fichas_psicologicas_cie", columnList = "catalogo_cie10_id")
    }
)
public class FichaPsicologica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_fichas_psicologicas")
    @SequenceGenerator(name = "seq_fichas_psicologicas", sequenceName = "seq_fichas_psicologicas", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personal_militar_id", nullable = false)
    private PersonalMilitar personalMilitar;

    @ManyToOne(optional = false)
    @JoinColumn(name = "psicologo_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private Psicologo psicologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_psicologo_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private Psicologo creadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actualizado_por_psicologo_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private Psicologo actualizadoPor;

    @Column(nullable = false, unique = true, length = 40)
    private String numeroEvaluacion;

    @Column(nullable = false)
    private LocalDate fechaEvaluacion;

    @Column(length = 40)
    private String tipoEvaluacion;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "observacionClinica", column = @Column(name = "observacion_clinica", columnDefinition = "CLOB")),
        @AttributeOverride(name = "motivoConsulta", column = @Column(name = "motivo_consulta", columnDefinition = "CLOB")),
        @AttributeOverride(name = "enfermedadActual", column = @Column(name = "enfermedad_actual", columnDefinition = "CLOB"))
    })
    private ObservacionClinica seccionObservacion;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "condicionesBiologicasPadres", column = @Column(name = "prenatal_condiciones_biologicas", columnDefinition = "CLOB")),
        @AttributeOverride(name = "condicionesPsicologicasPadres", column = @Column(name = "prenatal_condiciones_psicologicas", columnDefinition = "CLOB")),
        @AttributeOverride(name = "observacion", column = @Column(name = "prenatal_observacion", columnDefinition = "CLOB"))
    })
    private PsicoanamnesisPrenatal seccionPrenatal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "partoNormal", column = @Column(name = "natal_parto_normal")),
        @AttributeOverride(name = "termino", column = @Column(name = "natal_termino", length = 50)),
        @AttributeOverride(name = "complicaciones", column = @Column(name = "natal_complicaciones", length = 500)),
        @AttributeOverride(name = "observacion", column = @Column(name = "natal_observacion", columnDefinition = "CLOB"))
    })
    private PsicoanamnesisNatal seccionNatal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "gradoSociabilidad", column = @Column(name = "infancia_grado_sociabilidad")),
        @AttributeOverride(name = "relacionPadresHermanos", column = @Column(name = "infancia_relacion_padres_hermanos")),
        @AttributeOverride(name = "discapacidadIntelectual", column = @Column(name = "infancia_discapacidad_intelectual")),
        @AttributeOverride(name = "gradoDiscapacidad", column = @Column(name = "infancia_grado_discapacidad")),
        @AttributeOverride(name = "trastornos", column = @Column(name = "infancia_trastornos", length = 500)),
        @AttributeOverride(name = "tratamientosPsicologicosPsiquiatricos", column = @Column(name = "infancia_tratamientos_psico")),
        @AttributeOverride(name = "observacion", column = @Column(name = "infancia_observacion", columnDefinition = "CLOB"))
    })
    private PsicoanamnesisInfancia seccionInfancia;

    // NUEVA SECCIÓN: 6. Adolescencia, Juventud y Adultez (7 campos)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "habilidadesSociales", column = @Column(name = "adolescencia_habilidades_sociales", columnDefinition = "CLOB")),
        @AttributeOverride(name = "trastorno", column = @Column(name = "adolescencia_trastorno", columnDefinition = "CLOB")),
        @AttributeOverride(name = "historiaPersonal", column = @Column(name = "adolescencia_historia_personal", columnDefinition = "CLOB")),
        @AttributeOverride(name = "maltratoAdultoProblemasNegligencia", column = @Column(name = "adolescencia_maltrato_adulto", columnDefinition = "CLOB")),
        @AttributeOverride(name = "problemasRelacionadosCircunstanciasLegales", column = @Column(name = "adolescencia_problemas_legales", columnDefinition = "CLOB")),
        @AttributeOverride(name = "tratamientosPsicologicosPsiquiatricos", column = @Column(name = "adolescencia_tratamientos_psicologicos", columnDefinition = "CLOB")),
        @AttributeOverride(name = "observacion", column = @Column(name = "adolescencia_observacion", columnDefinition = "CLOB"))
    })
    private PsicoanamnesisAdolescenciaJuventudAdultez seccionAdolescencia;

    // NUEVA SECCIÓN: 7. PSICOANAMNESIS FAMILIAR, NORMAL Y PATOLÓGICA (5 campos)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "miembrosConQuienesConvive", column = @Column(name = "familiar_miembros_convive", columnDefinition = "CLOB")),
        @AttributeOverride(name = "antecedentesPatologicosFamiliares", column = @Column(name = "familiar_antecedentes_patologicos", columnDefinition = "CLOB")),
        @AttributeOverride(name = "tieneAlgunaEnfermedad", column = @Column(name = "familiar_tiene_enfermedad", columnDefinition = "CLOB")),
        @AttributeOverride(name = "tipoEnfermedad", column = @Column(name = "familiar_tipo_enfermedad", columnDefinition = "CLOB")),
        @AttributeOverride(name = "observacion", column = @Column(name = "familiar_observacion", columnDefinition = "CLOB"))
    })
    private PsicoanamnesisFamiliar seccionFamiliar;

    // NUEVA SECCIÓN: 8. EXÁMENES DE FUNCIONES PSICOLÓGICAS (12 campos)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "orientacion", column = @Column(name = "examen_orientacion", columnDefinition = "CLOB")),
        @AttributeOverride(name = "atencion", column = @Column(name = "examen_atencion", columnDefinition = "CLOB")),
        @AttributeOverride(name = "sensopercepciones", column = @Column(name = "examen_sensopercepciones", columnDefinition = "CLOB")),
        @AttributeOverride(name = "voluntad", column = @Column(name = "examen_voluntad", columnDefinition = "CLOB")),
        @AttributeOverride(name = "juicioRazonamiento", column = @Column(name = "examen_juicio_razonamiento", columnDefinition = "CLOB")),
        @AttributeOverride(name = "nutricion", column = @Column(name = "examen_nutricion", columnDefinition = "CLOB")),
        @AttributeOverride(name = "sueno", column = @Column(name = "examen_sueno", columnDefinition = "CLOB")),
        @AttributeOverride(name = "sexual", column = @Column(name = "examen_sexual", columnDefinition = "CLOB")),
        @AttributeOverride(name = "pensamientoCurso", column = @Column(name = "examen_pensamiento_curso", columnDefinition = "CLOB")),
        @AttributeOverride(name = "pensamientoEstructura", column = @Column(name = "examen_pensamiento_estructura", columnDefinition = "CLOB")),
        @AttributeOverride(name = "pensamientoContenido", column = @Column(name = "examen_pensamiento_contenido", columnDefinition = "CLOB")),
        @AttributeOverride(name = "concienciaEnfermedadTratamiento", column = @Column(name = "examen_conciencia_enfermedad", columnDefinition = "CLOB"))
    })
    private ExamenFuncionesPsicologicas seccionFuncionesPsicologicas;

    // NUEVA SECCIÓN: 10. RASGOS DE PERSONALIDAD y 11. EXÁMENES PSICOLÓGICOS (3 campos)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rasgo", column = @Column(name = "rasgos_personalidad_rasgo", columnDefinition = "CLOB")),
        @AttributeOverride(name = "observacion", column = @Column(name = "rasgos_observacion", columnDefinition = "CLOB")),
        @AttributeOverride(name = "examenesPsicologicos", column = @Column(name = "examenes_psicologicos", columnDefinition = "CLOB"))
    })
    private DiagnosticoRasgosExamenesPsicologicos seccionRasgosExamenes;

    // NUEVA SECCIÓN: 12. FORMULACIÓN ETIOPATOGÉNICA y 13. PRONÓSTICO (4 campos)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "factoresPredisponentes", column = @Column(name = "etiopatogenica_factores_predisponentes", columnDefinition = "CLOB")),
        @AttributeOverride(name = "factoresDeterminantes", column = @Column(name = "etiopatogenica_factores_determinantes", columnDefinition = "CLOB")),
        @AttributeOverride(name = "factoresDesencadenantes", column = @Column(name = "etiopatogenica_factores_desencadenantes", columnDefinition = "CLOB")),
        @AttributeOverride(name = "pronosticoTipo", column = @Column(name = "pronostico_tipo", columnDefinition = "CLOB"))
    })
    private FormulacionEtiopatogenicaPronostico seccionEtiopatogenicaPronostico;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 30)
    private EstadoFichaEnum estado = EstadoFichaEnum.ABIERTA;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "condicion_clinica", nullable = false, length = 40)
    private CondicionClinicaEnum condicionClinica = CondicionClinicaEnum.ALTA;


    // Ahora la ficha puede tener varios diagnósticos CIE-10
    @ManyToMany
    @JoinTable(
        name = "ficha_diagnosticos_cie10",
        joinColumns = @JoinColumn(name = "ficha_id"),
        inverseJoinColumns = @JoinColumn(name = "diagnostico_id")
    )
    private java.util.List<CatalogoDiagnosticoCie10> diagnosticosCie10;

    @Embedded
    private PlanSeguimiento planSeguimiento;

    @Embedded
    private TransferenciaInfo transferenciaInfo;

    @Column(name = "ultima_fecha_seguimiento")
    private LocalDate ultimaFechaSeguimiento;

    @Column(name = "proximo_seguimiento")
    private LocalDate proximoSeguimiento;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (fechaEvaluacion == null) {
            fechaEvaluacion = LocalDate.now();
        }
        if (seccionObservacion == null) {
            seccionObservacion = new ObservacionClinica();
        }
        if (seccionPrenatal == null) {
            seccionPrenatal = new PsicoanamnesisPrenatal();
        }
        if (seccionNatal == null) {
            seccionNatal = new PsicoanamnesisNatal();
        }
        if (seccionInfancia == null) {
            seccionInfancia = new PsicoanamnesisInfancia();
        }
        // Inicializar nuevas secciones
        if (seccionAdolescencia == null) {
            seccionAdolescencia = new PsicoanamnesisAdolescenciaJuventudAdultez();
        }
        if (seccionFamiliar == null) {
            seccionFamiliar = new PsicoanamnesisFamiliar();
        }
        if (seccionFuncionesPsicologicas == null) {
            seccionFuncionesPsicologicas = new ExamenFuncionesPsicologicas();
        }
        if (seccionRasgosExamenes == null) {
            seccionRasgosExamenes = new DiagnosticoRasgosExamenesPsicologicos();
        }
        if (seccionEtiopatogenicaPronostico == null) {
            seccionEtiopatogenicaPronostico = new FormulacionEtiopatogenicaPronostico();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}