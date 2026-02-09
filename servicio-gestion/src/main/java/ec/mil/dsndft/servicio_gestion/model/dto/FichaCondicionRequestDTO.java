package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class FichaCondicionRequestDTO {
    public java.util.List<DiagnosticoCie10DTO> getDiagnosticosCie10() {
        return diagnosticosCie10;
    }

    public void setDiagnosticosCie10(java.util.List<DiagnosticoCie10DTO> diagnosticosCie10) {
        this.diagnosticosCie10 = diagnosticosCie10;
    }

    @NotBlank(message = "La condición clínica es obligatoria")
    @JsonAlias({"condicion"})
    private String condicion;

    // Observación clínica asociada a la asignación de condición
    // (permite ALTA con solo observación, y registrar el texto para SEGUIMIENTO/TRANSFERENCIA)
    @Size(max = 4000, message = "La observación no debe superar 4000 caracteres")
    @JsonAlias({"observaciones", "observacion", "observacion_clinica", "observacionClinica"})
    private String observaciones;

    // Lista de diagnósticos CIE-10
    @JsonAlias({"diagnosticos_cie10"})
    private java.util.List<DiagnosticoCie10DTO> diagnosticosCie10;

    @JsonAlias({"plan_frecuencia"})
    private String planFrecuencia;

    @JsonAlias({"plan_tipo_sesion"})
    private String planTipoSesion;

    @Size(max = 500, message = "El detalle del plan no debe superar 500 caracteres")
    @JsonAlias({"plan_detalle"})
    private String planDetalle;

    @JsonAlias({"proximo_seguimiento"})
    private LocalDate proximoSeguimiento;

    @JsonAlias({"transferencia_unidad"})
    private String transferenciaUnidad;

    @JsonAlias({"transferencia_observacion"})
    private String transferenciaObservacion;

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }


    public String getPlanFrecuencia() {
        return planFrecuencia;
    }

    public void setPlanFrecuencia(String planFrecuencia) {
        this.planFrecuencia = planFrecuencia;
    }

    public String getPlanTipoSesion() {
        return planTipoSesion;
    }

    public void setPlanTipoSesion(String planTipoSesion) {
        this.planTipoSesion = planTipoSesion;
    }

    public String getPlanDetalle() {
        return planDetalle;
    }

    public void setPlanDetalle(String planDetalle) {
        this.planDetalle = planDetalle;
    }

    public LocalDate getProximoSeguimiento() {
        return proximoSeguimiento;
    }

    public void setProximoSeguimiento(LocalDate proximoSeguimiento) {
        this.proximoSeguimiento = proximoSeguimiento;
    }

    public String getTransferenciaUnidad() {
        return transferenciaUnidad;
    }

    public void setTransferenciaUnidad(String transferenciaUnidad) {
        this.transferenciaUnidad = transferenciaUnidad;
    }

    public String getTransferenciaObservacion() {
        return transferenciaObservacion;
    }

    public void setTransferenciaObservacion(String transferenciaObservacion) {
        this.transferenciaObservacion = transferenciaObservacion;
    }
}
