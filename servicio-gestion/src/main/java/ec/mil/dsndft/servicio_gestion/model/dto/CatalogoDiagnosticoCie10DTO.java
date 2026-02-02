package ec.mil.dsndft.servicio_gestion.model.dto;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = CatalogoDiagnosticoCie10DTO.CatalogoDiagnosticoCie10DTOBuilder.class)
public class CatalogoDiagnosticoCie10DTO {
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Long id;
    @com.fasterxml.jackson.annotation.JsonProperty("codigo")
    private String codigo;
    @com.fasterxml.jackson.annotation.JsonProperty("nombre")
    private String nombre;
    @com.fasterxml.jackson.annotation.JsonProperty("descripcion")
    private String descripcion;
    @com.fasterxml.jackson.annotation.JsonProperty("categoriaPadre")
    private String categoriaPadre;
    @com.fasterxml.jackson.annotation.JsonProperty("nivel")
    private Integer nivel;
    @com.fasterxml.jackson.annotation.JsonProperty("activo")
    private Boolean activo;
    @com.fasterxml.jackson.annotation.JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    @com.fasterxml.jackson.annotation.JsonProperty("fechaActualizacion")
    private LocalDateTime fechaActualizacion;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CatalogoDiagnosticoCie10DTOBuilder {
    }
}
