package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FichaPsicologicaCompletaRequestDTO {
    @NotNull(message = "Los datos generales son obligatorios")
    @Valid
    @JsonAlias({"datos_generales"})
    private FichaDatosGeneralesRequestDTO datosGenerales;
    @Valid
    @JsonAlias({"seccion_observacion"})
    private FichaSeccionObservacionRequestDTO seccionObservacion;
    @Valid
    @JsonAlias({"seccion_psicoanamnesis"})
    private FichaSeccionPsicoanamnesisRequestDTO seccionPsicoanamnesis;
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
    @Valid
    @JsonAlias({"seccion_diagnostico_condicion"})
    private FichaCondicionRequestDTO seccionDiagnosticoCondicion;
}
