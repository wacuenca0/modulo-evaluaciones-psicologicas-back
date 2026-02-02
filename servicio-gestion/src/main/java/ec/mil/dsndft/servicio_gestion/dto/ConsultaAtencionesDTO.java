package ec.mil.dsndft.servicio_gestion.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ConsultaAtencionesDTO {
    private Long psicologoId;
    private Long pacienteId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private String tipoConsulta;
    private String tipoAtencion;
}
