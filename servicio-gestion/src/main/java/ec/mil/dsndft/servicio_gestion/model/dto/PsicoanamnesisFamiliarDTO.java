package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PsicoanamnesisFamiliarDTO {
    @Size(max = 4000, message = "Los miembros con quienes convive no deben superar 4000 caracteres")
    @JsonAlias({"miembros_con_quienes_convive"})
    private String miembrosConQuienesConvive;
    @Size(max = 4000, message = "Los antecedentes patológicos no deben superar 4000 caracteres")
    @JsonAlias({"antecedentes_patologicos_familiares"})
    private String antecedentesPatologicosFamiliares;
    @Size(max = 4000, message = "La información sobre enfermedades no debe superar 4000 caracteres")
    @JsonAlias({"tiene_alguna_enfermedad"})
    private String tieneAlgunaEnfermedad;
    @Size(max = 4000, message = "El tipo de enfermedad no debe superar 4000 caracteres")
    @JsonAlias({"tipo_de_enfermedad"})
    private String tipoEnfermedad;
    @Size(max = 4000, message = "La observación no debe superar 4000 caracteres")
    @JsonAlias({"observacion"})
    private String observacion;
}
