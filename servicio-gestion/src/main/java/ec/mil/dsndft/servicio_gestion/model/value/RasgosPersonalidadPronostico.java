package ec.mil.dsndft.servicio_gestion.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RasgosPersonalidadPronostico {

    @Column(name = "rasgos_personalidad_diagnostico", columnDefinition = "CLOB")
    private String diagnostico;

    @Column(name = "rasgos_personalidad_rasgo", columnDefinition = "CLOB")
    private String rasgo;

    @Column(name = "rasgos_personalidad_observacion", columnDefinition = "CLOB")
    private String observacion;

    @Column(name = "rasgos_personalidad_examenes_psicologicos", columnDefinition = "CLOB")
    private String examenesPsicologicos;

    @Column(name = "pronostico_tipo", columnDefinition = "CLOB")
    private String pronosticoTipo;

    @Column(name = "recomendaciones", columnDefinition = "CLOB")
    private String recomendaciones;
}