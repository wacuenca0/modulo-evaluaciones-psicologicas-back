package ec.mil.dsndft.servicio_gestion.model.dto;

import lombok.Data;

@Data
public class DiagnosticoCie10DTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String categoriaPadre;
    private Integer nivel;
    private String descripcion;
}
