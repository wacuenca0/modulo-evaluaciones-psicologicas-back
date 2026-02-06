package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteAtencionPsicologoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteHistorialFichaDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReportePersonalDiagnosticoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteSeguimientoTransferenciaDTO;
import ec.mil.dsndft.servicio_gestion.service.ReporteGestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes de gestión", description = "Reportes estadísticos y de seguimiento clínico")
public class ReporteGestionController {

    private final ReporteGestionService reporteGestionService;

    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/atenciones-psicologos")
    @Operation(summary = "Reporte de atenciones por psicólogo",
               description = "Devuelve estadísticas de atenciones asignadas y acciones realizadas por psicólogo, con filtros y paginación")
    public ResponseEntity<Page<ReporteAtencionPsicologoDTO>> obtenerAtenciones(
        @RequestParam(required = false) Long psicologoId,
        @RequestParam(required = false) String psicologoCedula,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(required = false) Long diagnosticoId,
        @RequestParam(required = false) String cedula,
        @RequestParam(required = false) String unidadMilitar
    , @PageableDefault(size = 10) Pageable pageable) {
        Page<ReporteAtencionPsicologoDTO> resultado = reporteGestionService
            .obtenerAtencionesPorPsicologo(psicologoId, psicologoCedula, fechaDesde, fechaHasta, diagnosticoId, cedula, unidadMilitar, pageable);
        return ResponseEntity.ok(resultado);
    }


    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/personal-diagnosticos")
    @Operation(summary = "Reporte de personal por diagnósticos",
               description = "Muestra personal atendido agrupado por diagnósticos, con filtros de fecha, cédula, grado y unidad")
    public ResponseEntity<Page<ReportePersonalDiagnosticoDTO>> obtenerPersonalDiagnosticos(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(required = false) Long diagnosticoId,
        @RequestParam(required = false) String cedula,
        @RequestParam(required = false) String grado,
        @RequestParam(required = false) String unidadMilitar
    , @PageableDefault(size = 10) Pageable pageable) {
        Page<ReportePersonalDiagnosticoDTO> resultado = reporteGestionService
            .obtenerReportePersonalDiagnostico(fechaDesde, fechaHasta, diagnosticoId, cedula, grado, unidadMilitar, pageable);
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/seguimiento-transferencia")
    @Operation(summary = "Reporte de seguimiento y transferencia",
               description = "Lista fichas en seguimiento y transferencias entre psicólogos/unidades")
    public ResponseEntity<Page<ReporteSeguimientoTransferenciaDTO>> obtenerSeguimientoTransferencia(
        @RequestParam(required = false) Long psicologoId,
        @RequestParam(required = false) String psicologoCedula,
        @RequestParam(required = false) String cedula,
        @RequestParam(required = false) String unidadMilitar,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(defaultValue = "false") boolean incluirSeguimientos
    , @PageableDefault(size = 10) Pageable pageable) {
        Page<ReporteSeguimientoTransferenciaDTO> resultado = reporteGestionService.obtenerReporteSeguimientoTransferencia(
            psicologoId, psicologoCedula, cedula, unidadMilitar, fechaDesde, fechaHasta, incluirSeguimientos, pageable
        );
        return ResponseEntity.ok(resultado);
    }
    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/historial-fichas")
    @Operation(summary = "Historial de fichas psicológicas",
               description = "Obtiene el historial clínico de fichas por persona, con opción de incluir seguimientos")
    public ResponseEntity<Page<ReporteHistorialFichaDTO>> obtenerHistorialFichas(
        @RequestParam(required = false) Long personalMilitarId,
        @RequestParam(required = false) String cedula,
        @RequestParam(defaultValue = "false") boolean incluirSeguimientos
    , @PageableDefault(size = 10) Pageable pageable) {
        Page<ReporteHistorialFichaDTO> resultado = reporteGestionService.obtenerHistorialFichas(
            personalMilitarId, cedula, incluirSeguimientos, pageable
        );
        return ResponseEntity.ok(resultado);
    }
}
