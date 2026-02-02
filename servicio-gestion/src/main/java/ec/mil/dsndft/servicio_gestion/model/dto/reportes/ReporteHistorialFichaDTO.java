package ec.mil.dsndft.servicio_gestion.model.dto.reportes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReporteHistorialFichaDTO {
    private long totalSeguimientos;
    private String origen;
    private Long personalMilitarId;
    private String personalMilitarCedula;
    private String personalMilitarNombre;
    private Long fichaId;
    private Long fichaHistoricaId;
    private String numeroFicha;
    private LocalDate fechaEvaluacion;
    private String condicionClinica;
    private String estadoFicha;
    private String diagnosticoCodigo;
    private String diagnosticoNombre;
    private String diagnosticoCategoriaPadre;
    private Integer diagnosticoNivel;
    private String diagnosticoDescripcion;
    private Long psicologoId;
    private String psicologoNombre;
    private String psicologoUnidadMilitar;
    private Long seguimientosCantidad;
    private boolean tieneSeguimientos;
    private Long filtroPersonalMilitarId;
    private String filtroCedula;
    private boolean filtroIncluirSeguimientos;

    public long getTotalSeguimientos() {
        return totalSeguimientos;
    }

    public void setTotalSeguimientos(long totalSeguimientos) {
        this.totalSeguimientos = totalSeguimientos;
    }
}
