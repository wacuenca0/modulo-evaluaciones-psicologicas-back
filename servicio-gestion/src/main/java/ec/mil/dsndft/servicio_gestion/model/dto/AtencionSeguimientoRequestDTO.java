package ec.mil.dsndft.servicio_gestion.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AtencionSeguimientoRequestDTO {
    private Long fichaPsicologicaId;
    private Long psicologoId;
    private LocalDate fechaAtencion;
    private String horaInicio;
    private String horaFin;
    private String motivoConsulta;
    private String planIntervencion;
    private String recomendaciones;
    private List<Long> diagnosticoIds;
    private LocalDate proximaCita;
    private String observacionesProximaCita;
}
