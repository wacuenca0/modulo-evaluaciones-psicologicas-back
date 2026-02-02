package ec.mil.dsndft.servicio_gestion.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AtencionPsicologicaRequestDTO {
    private Long personalMilitarId;
    private Long psicologoId;
    private LocalDate fechaAtencion;
    private String horaInicio;
    private String horaFin;
    private String tipoAtencion;
    private String tipoConsulta;
    private String motivoConsulta;
    private String anamnesis;
    private String examenMental;
    private String impresionDiagnostica;
    private String planIntervencion;
    private String recomendaciones;
    private String derivacion;
    private List<Long> diagnosticoIds;
    private LocalDate proximaCita;
    private String observacionesProximaCita;
    private String estado;
    private String razonCancelacion;
}