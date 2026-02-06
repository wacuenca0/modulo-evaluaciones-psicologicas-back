package ec.mil.dsndft.servicio_gestion.service;

import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteAtencionPsicologoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReportePersonalDiagnosticoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteHistorialFichaDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteSeguimientoTransferenciaDTO;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReporteGestionService {

    Page<ReporteAtencionPsicologoDTO> obtenerAtencionesPorPsicologo(Long psicologoId,
                                                                    String psicologoCedula,
                                                                    LocalDate fechaDesde,
                                                                    LocalDate fechaHasta,
                                                                    Long diagnosticoId,
                                                                    String cedula,
                                                                    String unidadMilitar,
                                                                    Pageable pageable);

    Page<ReportePersonalDiagnosticoDTO> obtenerReportePersonalDiagnostico(LocalDate fechaDesde,
                                                                          LocalDate fechaHasta,
                                                                          Long diagnosticoId,
                                                                          String cedula,
                                                                          String grado,
                                                                          String unidadMilitar,
                                                                          Pageable pageable);

    Page<ReporteHistorialFichaDTO> obtenerHistorialFichas(Long personalMilitarId,
                                                          String cedula,
                                                          boolean incluirSeguimientos,
                                                          Pageable pageable);

    /**
     * Reporte de personal militar en condición de Seguimiento o Transferencia.
     * @param psicologoId filtro opcional por psicólogo
     * @param cedula filtro opcional por cédula
     * @param unidadMilitar filtro opcional por unidad militar
     * @param fechaDesde filtro opcional por fecha desde
     * @param fechaHasta filtro opcional por fecha hasta
     * @param incluirSeguimientos si true, cuenta atenciones con tipoConsulta 'SEGUIMIENTO'
     */
    Page<ReporteSeguimientoTransferenciaDTO> obtenerReporteSeguimientoTransferencia(Long psicologoId,
                                                                                   String psicologoCedula,
                                                                                   String cedula,
                                                                                   String unidadMilitar,
                                                                                   LocalDate fechaDesde,
                                                                                   LocalDate fechaHasta,
                                                                                   boolean incluirSeguimientos,
                                                                                   Pageable pageable);
}
