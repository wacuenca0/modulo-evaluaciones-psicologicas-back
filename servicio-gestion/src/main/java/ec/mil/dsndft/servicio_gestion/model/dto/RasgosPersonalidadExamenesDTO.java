package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RasgosPersonalidadExamenesDTO {
    @Size(max = 4000, message = "Los rasgos no deben superar 4000 caracteres")
    @JsonAlias({"rasgo"})
    private String rasgo;
    @Size(max = 4000, message = "La observación no debe superar 4000 caracteres")
    @JsonAlias({"observacion"})
    private String observacion;
    @Size(max = 4000, message = "Los exámenes psicológicos no deben superar 4000 caracteres")
    @JsonAlias({"examenes_psicologicos"})
    private String examenesPsicologicos;
}
