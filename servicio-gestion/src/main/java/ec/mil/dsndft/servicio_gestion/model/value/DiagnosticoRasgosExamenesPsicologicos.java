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
public class DiagnosticoRasgosExamenesPsicologicos {
    @Column(name = "rasgos_personalidad_rasgo", columnDefinition = "CLOB")
    private String rasgo;
    @Column(name = "rasgos_observacion", columnDefinition = "CLOB")
    private String observacion;
    @Column(name = "examenes_psicologicos", columnDefinition = "CLOB")
    private String examenesPsicologicos;
}
