package ec.mil.dsndft.servicio_gestion.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AtencionPsicologicaResponseDTO {
    private Long id;
    private Long fichaPsicologicaId;
    
    // Información del paciente
    private Long personalMilitarId;
    private String pacienteCedula;
    private String pacienteNombreCompleto;
    private String pacienteGrado;
    private String pacienteUnidadMilitar;
    
    // Información del psicólogo
    private Long psicologoId;
    private String psicologoCedula;
    private String psicologoNombreCompleto;
    
    // Información de la atención
    private LocalDate fechaAtencion;
    private String horaInicio;
    private String horaFin;
    private Integer numeroSesion;
    private String tipoAtencion;
    private String tipoConsulta;
    
    // Datos clínicos
    private String motivoConsulta;
    private String anamnesis;
    private String examenMental;
    private String impresionDiagnostica;
    private String planIntervencion;
    private String recomendaciones;
    private String derivacion;
    
    // Diagnósticos
    private List<DiagnosticoCie10DTO> diagnosticos;
    
    // Próxima cita
    private LocalDate proximaCita;
    private String observacionesProximaCita;
    
    // Estado
    private String estado;
    private String razonCancelacion;
    private Boolean activo;
    
    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Información calculada
    private Integer duracionMinutos;

    // Tipo de evaluación
    @com.fasterxml.jackson.annotation.JsonProperty("tipoEvaluacion")
    private String tipoEvaluacion;
    
    @Data
    public static class DiagnosticoCie10DTO {
        private Long id;
        private String codigo;
        private String nombre;
        private String descripcion;
    }
}