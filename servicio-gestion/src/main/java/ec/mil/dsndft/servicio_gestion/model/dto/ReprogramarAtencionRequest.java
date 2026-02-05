package ec.mil.dsndft.servicio_gestion.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReprogramarAtencionRequest {
    private LocalDate fechaAtencion;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipoAtencion;
    private String motivoReprogramacion;
    private String estado = "PROGRAMADA";

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }
    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public LocalTime getHoraFin() {
        return horaFin;
    }
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
    public String getTipoAtencion() {
        return tipoAtencion;
    }
    public void setTipoAtencion(String tipoAtencion) {
        this.tipoAtencion = tipoAtencion;
    }
    public String getMotivoReprogramacion() {
        return motivoReprogramacion;
    }
    public void setMotivoReprogramacion(String motivoReprogramacion) {
        this.motivoReprogramacion = motivoReprogramacion;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
