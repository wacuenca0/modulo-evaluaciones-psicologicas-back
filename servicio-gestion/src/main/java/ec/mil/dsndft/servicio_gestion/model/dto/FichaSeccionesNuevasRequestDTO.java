package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class FichaSeccionesNuevasRequestDTO {
    @Valid
    @JsonAlias({"seccion_adolescencia"})
    private AdolescenciaJuventudAdultezDTO seccionAdolescencia;
    @Valid
    @JsonAlias({"seccion_familiar"})
    private PsicoanamnesisFamiliarDTO seccionFamiliar;
    @Valid
    @JsonAlias({"seccion_funciones_psicologicas"})
    private ExamenFuncionesPsicologicasDTO seccionFuncionesPsicologicas;
    @Valid
    @JsonAlias({"seccion_rasgos_examenes"})
    private RasgosPersonalidadExamenesDTO seccionRasgosExamenes;
    @Valid
    @JsonAlias({"seccion_etiopatogenica_pronostico"})
    private FormulacionEtiopatogenicaPronosticoDTO seccionEtiopatogenicaPronostico;
}
