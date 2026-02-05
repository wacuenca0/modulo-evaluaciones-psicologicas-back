package ec.mil.dsndft.servicio_gestion.model.dto;

import java.time.LocalDateTime;

public class AtencionPsicologicaHistorialDTO {
    private Long id;
    private String estado;
    private String razonCambio;
    private LocalDateTime fechaCambio;
    private Long psicologoId;
    private String psicologoNombres;
    private String psicologoApellidos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getRazonCambio() { return razonCambio; }
    public void setRazonCambio(String razonCambio) { this.razonCambio = razonCambio; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
    public Long getPsicologoId() { return psicologoId; }
    public void setPsicologoId(Long psicologoId) { this.psicologoId = psicologoId; }
    public String getPsicologoNombres() { return psicologoNombres; }
    public void setPsicologoNombres(String psicologoNombres) { this.psicologoNombres = psicologoNombres; }
    public String getPsicologoApellidos() { return psicologoApellidos; }
    public void setPsicologoApellidos(String psicologoApellidos) { this.psicologoApellidos = psicologoApellidos; }
}
