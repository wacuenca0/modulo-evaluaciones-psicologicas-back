package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdolescenciaJuventudAdultezDTO {
    @Size(max = 4000, message = "Las habilidades sociales no deben superar 4000 caracteres")
    @JsonAlias({"habilidades_sociales"})
    private String habilidadesSociales;
    @Size(max = 4000, message = "Los trastornos no deben superar 4000 caracteres")
    @JsonAlias({"trastorno"})
    private String trastorno;
    @Size(max = 4000, message = "La historia personal no debe superar 4000 caracteres")
    @JsonAlias({"historia_personal"})
    private String historiaPersonal;
    @Size(max = 4000, message = "El maltrato adulto no debe superar 4000 caracteres")
    @JsonAlias({"maltrato_adulto_problemas_negligencia"})
    private String maltratoAdultoProblemasNegligencia;
    @Size(max = 4000, message = "Los problemas legales no deben superar 4000 caracteres")
    @JsonAlias({"problemas_relacionados_circunstancias_legales"})
    private String problemasRelacionadosCircunstanciasLegales;
    @Size(max = 4000, message = "Los tratamientos no deben superar 4000 caracteres")
    @JsonAlias({"tratamientos_psicologicos_psiquiatricos"})
    private String tratamientosPsicologicosPsiquiatricos;
    @Size(max = 4000, message = "La observación no debe superar 4000 caracteres")
    @JsonAlias({"observacion"})
    private String observacion;
}
