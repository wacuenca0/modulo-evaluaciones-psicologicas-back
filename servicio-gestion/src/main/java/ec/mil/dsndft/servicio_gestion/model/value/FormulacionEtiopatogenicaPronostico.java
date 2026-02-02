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
public class FormulacionEtiopatogenicaPronostico {
    @Column(name = "etiopatogenica_factores_predisponentes", columnDefinition = "CLOB")
    private String factoresPredisponentes;
    @Column(name = "etiopatogenica_factores_determinantes", columnDefinition = "CLOB")
    private String factoresDeterminantes;
    @Column(name = "etiopatogenica_factores_desencadenantes", columnDefinition = "CLOB")
    private String factoresDesencadenantes;
    @Column(name = "pronostico_tipo", columnDefinition = "CLOB")
    private String pronosticoTipo;
}
