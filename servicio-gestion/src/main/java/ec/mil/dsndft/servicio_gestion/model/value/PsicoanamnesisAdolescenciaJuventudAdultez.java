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
public class PsicoanamnesisAdolescenciaJuventudAdultez {
    @Column(name = "adolescencia_habilidades_sociales", columnDefinition = "CLOB")
    private String habilidadesSociales;
    @Column(name = "adolescencia_trastorno", columnDefinition = "CLOB")
    private String trastorno;
    @Column(name = "adolescencia_historia_personal", columnDefinition = "CLOB")
    private String historiaPersonal;
    @Column(name = "adolescencia_maltrato_adulto", columnDefinition = "CLOB")
    private String maltratoAdultoProblemasNegligencia;
    @Column(name = "adolescencia_problemas_legales", columnDefinition = "CLOB")
    private String problemasRelacionadosCircunstanciasLegales;
    @Column(name = "adolescencia_tratamientos_psicologicos", columnDefinition = "CLOB")
    private String tratamientosPsicologicosPsiquiatricos;
    @Column(name = "adolescencia_observacion", columnDefinition = "CLOB")
    private String observacion;
}