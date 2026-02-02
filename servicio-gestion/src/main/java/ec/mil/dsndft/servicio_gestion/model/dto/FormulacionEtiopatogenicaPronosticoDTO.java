package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FormulacionEtiopatogenicaPronosticoDTO {
    @Size(max = 4000, message = "Los factores predisponentes no deben superar 4000 caracteres")
    @JsonAlias({"factores_predisponentes"})
    private String factoresPredisponentes;
    @Size(max = 4000, message = "Los factores determinantes no deben superar 4000 caracteres")
    @JsonAlias({"factores_determinantes"})
    private String factoresDeterminantes;
    @Size(max = 4000, message = "Los factores desencadenantes no deben superar 4000 caracteres")
    @JsonAlias({"factores_desencadenantes"})
    private String factoresDesencadenantes;
    @Size(max = 4000, message = "El tipo de pronóstico no debe superar 4000 caracteres")
    @JsonAlias({"pronostico_tipo"})
    private String pronosticoTipo;
}
