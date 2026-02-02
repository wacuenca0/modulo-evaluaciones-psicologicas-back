package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteAtencionPsicologoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteHistorialFichaDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReportePersonalDiagnosticoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteSeguimientoTransferenciaDTO;
import ec.mil.dsndft.servicio_gestion.service.ReporteGestionService;
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
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteGestionController {

    private final ReporteGestionService reporteGestionService;

    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/atenciones-psicologos")
    public ResponseEntity<Page<ReporteAtencionPsicologoDTO>> obtenerAtenciones(
        @RequestParam(required = false) Long psicologoId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(required = false) Long diagnosticoId,
        @RequestParam(required = false) String cedula,
        @RequestParam(required = false) String unidadMilitar
    , Pageable pageable) {
        Page<ReporteAtencionPsicologoDTO> resultado = reporteGestionService
            .obtenerAtencionesPorPsicologo(psicologoId, fechaDesde, fechaHasta, diagnosticoId, cedula, unidadMilitar, pageable);
        return ResponseEntity.ok(resultado);
    }


    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/personal-diagnosticos")
    public ResponseEntity<Page<ReportePersonalDiagnosticoDTO>> obtenerPersonalDiagnosticos(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(required = false) Long diagnosticoId,
        @RequestParam(required = false) String cedula,
        @RequestParam(required = false) String grado,
        @RequestParam(required = false) String unidadMilitar
    , Pageable pageable) {
        Page<ReportePersonalDiagnosticoDTO> resultado = reporteGestionService
            .obtenerReportePersonalDiagnostico(fechaDesde, fechaHasta, diagnosticoId, cedula, grado, unidadMilitar, pageable);
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/seguimiento-transferencia")
    public ResponseEntity<Page<ReporteSeguimientoTransferenciaDTO>> obtenerSeguimientoTransferencia(
        @RequestParam(required = false) Long psicologoId,
        @RequestParam(required = false) String cedula,
        @RequestParam(required = false) String unidadMilitar,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(defaultValue = "false") boolean incluirSeguimientos
    , Pageable pageable) {
        Page<ReporteSeguimientoTransferenciaDTO> resultado = reporteGestionService.obtenerReporteSeguimientoTransferencia(
            psicologoId, cedula, unidadMilitar, fechaDesde, fechaHasta, incluirSeguimientos, pageable
        );
        return ResponseEntity.ok(resultado);
    }
    @PreAuthorize("hasRole('OBSERVADOR')")
    @GetMapping("/historial-fichas")
    public ResponseEntity<Page<ReporteHistorialFichaDTO>> obtenerHistorialFichas(
        @RequestParam(required = false) Long personalMilitarId,
        @RequestParam(required = false) String cedula,
        @RequestParam(defaultValue = "false") boolean incluirSeguimientos
    , Pageable pageable) {
        Page<ReporteHistorialFichaDTO> resultado = reporteGestionService.obtenerHistorialFichas(
            personalMilitarId, cedula, incluirSeguimientos, pageable
        );
        return ResponseEntity.ok(resultado);
    }
}
