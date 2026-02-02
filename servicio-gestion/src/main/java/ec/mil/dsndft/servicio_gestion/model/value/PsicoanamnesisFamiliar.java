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
public class PsicoanamnesisFamiliar {
    @Column(name = "familiar_miembros_convive", columnDefinition = "CLOB")
    private String miembrosConQuienesConvive;
    @Column(name = "familiar_antecedentes_patologicos", columnDefinition = "CLOB")
    private String antecedentesPatologicosFamiliares;
    @Column(name = "familiar_tiene_enfermedad", columnDefinition = "CLOB")
    private String tieneAlgunaEnfermedad;
    @Column(name = "familiar_tipo_enfermedad", columnDefinition = "CLOB")
    private String tipoEnfermedad;
    @Column(name = "familiar_observacion", columnDefinition = "CLOB")
    private String observacion;
}