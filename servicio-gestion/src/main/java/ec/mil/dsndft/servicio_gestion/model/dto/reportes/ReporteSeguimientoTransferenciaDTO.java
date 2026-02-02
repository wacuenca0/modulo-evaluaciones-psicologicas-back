    package ec.mil.dsndft.servicio_gestion.model.dto.reportes;

    import ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReporteSeguimientoTransferenciaDTO {
    private Long personalMilitarId;
    private String personalMilitarNombre;
    private String personalMilitarCedula;
    private Long fichaId;
    private String numeroEvaluacion;
    private CondicionClinicaEnum condicionClinica;
    private Long psicologoId;
    private String psicologoNombre;
    private String psicologoUnidadMilitar;
    private Long totalSeguimientos;
    private LocalDate ultimaFechaSeguimiento;
    private LocalDate fechaEvaluacion;
    private String unidadTransferencia;
    private String observacionTransferencia;
    private LocalDate fechaTransferencia;
    private LocalDate proximoSeguimiento;

    // Campos para filtros y canonical (no van en la proyección JPQL, pero sí en el DTO)
    private String filtroCedula;
    private String filtroUnidadMilitar;
    private LocalDate filtroFechaDesde;
    private LocalDate filtroFechaHasta;
    private String condicionClinicaCanonical;

    // Constructor exactamente igual a la proyección JPQL
    public ReporteSeguimientoTransferenciaDTO(Long personalMilitarId,
                                              String personalMilitarNombre,
                                              String personalMilitarCedula,
                                              Long fichaId,
                                              String numeroEvaluacion,
                                              CondicionClinicaEnum condicionClinica,
                                              Long psicologoId,
                                              String psicologoNombre,
                                              String psicologoUnidadMilitar,
                                              Long totalSeguimientos,
                                              LocalDate ultimaFechaSeguimiento,
                                              LocalDate fechaEvaluacion,
                                              String unidadTransferencia,
                                              Object observacionTransferencia,
                                              LocalDate fechaTransferencia,
                                              LocalDate proximoSeguimiento) {
        this.personalMilitarId = personalMilitarId;
        this.personalMilitarNombre = personalMilitarNombre;
        this.personalMilitarCedula = personalMilitarCedula;
        this.fichaId = fichaId;
        this.numeroEvaluacion = numeroEvaluacion;
        this.condicionClinica = condicionClinica;
        this.psicologoId = psicologoId;
        this.psicologoNombre = psicologoNombre;
        this.psicologoUnidadMilitar = psicologoUnidadMilitar;
        this.totalSeguimientos = totalSeguimientos != null ? totalSeguimientos : 0L;
        this.ultimaFechaSeguimiento = ultimaFechaSeguimiento;
        this.fechaEvaluacion = fechaEvaluacion;
        this.unidadTransferencia = unidadTransferencia;
        this.observacionTransferencia = observacionTransferencia != null ? observacionTransferencia.toString() : null;
        this.fechaTransferencia = fechaTransferencia;
        this.proximoSeguimiento = proximoSeguimiento;
    }

    // Métodos set/get para los campos de filtro y canonical
    public void setFiltroCedula(String filtroCedula) { this.filtroCedula = filtroCedula; }
    public void setFiltroUnidadMilitar(String filtroUnidadMilitar) { this.filtroUnidadMilitar = filtroUnidadMilitar; }
    public void setFiltroFechaDesde(LocalDate filtroFechaDesde) { this.filtroFechaDesde = filtroFechaDesde; }
    public void setFiltroFechaHasta(LocalDate filtroFechaHasta) { this.filtroFechaHasta = filtroFechaHasta; }
    public void setCondicionClinicaCanonical(String condicionClinicaCanonical) { this.condicionClinicaCanonical = condicionClinicaCanonical; }
    public String getFiltroCedula() { return filtroCedula; }
    public String getFiltroUnidadMilitar() { return filtroUnidadMilitar; }
    public LocalDate getFiltroFechaDesde() { return filtroFechaDesde; }
    public LocalDate getFiltroFechaHasta() { return filtroFechaHasta; }
    public String getCondicionClinicaCanonical() { return condicionClinicaCanonical; }
}
