package ec.mil.dsndft.servicio_gestion.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FichaPsicologicaCompletaDTO {
    private Long id;
    private FichaDatosGeneralesRequestDTO datosGenerales;
    private FichaSeccionObservacionRequestDTO seccionObservacion;
    private FichaSeccionPsicoanamnesisRequestDTO seccionPsicoanamnesis;
    private AdolescenciaJuventudAdultezDTO seccionAdolescencia;
    private PsicoanamnesisFamiliarDTO seccionFamiliar;
    private ExamenFuncionesPsicologicasDTO seccionFuncionesPsicologicas;
    private RasgosPersonalidadExamenesDTO seccionRasgosExamenes;
    private FormulacionEtiopatogenicaPronosticoDTO seccionEtiopatogenicaPronostico;
    private FichaCondicionRequestDTO seccionDiagnosticoCondicion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
