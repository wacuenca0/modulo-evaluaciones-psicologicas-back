package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.model.dto.*;
import ec.mil.dsndft.servicio_gestion.service.FichaPsicologicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fichas-psicologicas")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.controllers.fichas", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FichaPsicologicaController {

    private final FichaPsicologicaService fichaPsicologicaService;

    // ============================================
    // ENDPOINTS EXISTENTES (sin cambios)
    // ============================================

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping
    public ResponseEntity<List<FichaPsicologicaDTO>> listarFichas(
        @RequestParam(required = false) Long psicologoId,
        @RequestParam(required = false) Long personalMilitarId,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String condicion,
        @RequestParam(required = false) Boolean soloActivas
    ) {
        List<FichaPsicologicaDTO> fichas = fichaPsicologicaService.listar(psicologoId, personalMilitarId, estado, condicion, soloActivas);
        return ResponseEntity.ok(fichas);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/condicion")
    public ResponseEntity<List<FichaPsicologicaDTO>> listarPorCondicion(
        @RequestParam String condicion,
        @RequestParam(required = false) Long psicologoId,
        @RequestParam(required = false) Long personalMilitarId
    ) {
        return ResponseEntity.ok(fichaPsicologicaService.listarPorCondicion(condicion, psicologoId, personalMilitarId));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/historial/{personalMilitarId}")
    public ResponseEntity<List<FichaPsicologicaDTO>> obtenerHistorial(@PathVariable Long personalMilitarId) {
        return ResponseEntity.ok(fichaPsicologicaService.obtenerHistorialPorPersonal(personalMilitarId));
    }

        // Nuevo endpoint: historial paginado con filtros (cédula del psicólogo y rango de fechas)
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
        @GetMapping("/historial/{personalMilitarId}/page")
        public ResponseEntity<ec.mil.dsndft.servicio_gestion.model.dto.FichaPsicologicaPageSafeDTO> obtenerHistorialPaginado(
            @PathVariable Long personalMilitarId,
            @RequestParam(required = false) String cedulaPsicologo,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaDesde,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<FichaPsicologicaDTO> result = fichaPsicologicaService.obtenerHistorialPorPersonalPaginado(
            personalMilitarId,
            cedulaPsicologo,
            fechaDesde,
            fechaHasta,
            pageable
        );
        ec.mil.dsndft.servicio_gestion.model.dto.FichaPsicologicaPageSafeDTO dto = new ec.mil.dsndft.servicio_gestion.model.dto.FichaPsicologicaPageSafeDTO(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isLast(),
            result.isFirst()
        );
        return ResponseEntity.ok(dto);
        }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/tipo-evaluacion/{id}")
    public ResponseEntity<?> obtenerTipoEvaluacion(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID inválido"));
            }
            FichaPsicologicaDTO ficha = fichaPsicologicaService.obtenerPorId(id);
            if (ficha == null) {
                return ResponseEntity.status(404).body(Map.of("error", "No existe ficha psicológica"));
            }
            if (ficha.getTipoEvaluacion() == null) {
                return ResponseEntity.status(404).body(Map.of("error", "No existe tipo de evaluación"));
            }
            return ResponseEntity.ok(Map.of("tipoEvaluacion", ficha.getTipoEvaluacion()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno"));
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/numero/{numeroEvaluacion}")
    public ResponseEntity<FichaPsicologicaDTO> obtenerPorNumero(@PathVariable String numeroEvaluacion) {
        return ResponseEntity.ok(fichaPsicologicaService.obtenerPorNumeroEvaluacion(numeroEvaluacion));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PostMapping
    public ResponseEntity<FichaPsicologicaDTO> crearFicha(@Valid @RequestBody FichaDatosGeneralesRequestDTO request) {
        FichaPsicologicaDTO creada = fichaPsicologicaService.crearFicha(request);
        return ResponseEntity.created(URI.create("/api/fichas-psicologicas/" + creada.getId())).body(creada);
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @GetMapping("/numero-preview")
    public ResponseEntity<Map<String, String>> generarNumeroPreview() {
        String numero = fichaPsicologicaService.generarNumeroEvaluacionPreview();
        return ResponseEntity.ok(Map.of("numeroEvaluacion", numero));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/general")
    public ResponseEntity<FichaPsicologicaDTO> actualizarDatosGenerales(@PathVariable Long id,
                                                                        @Valid @RequestBody FichaDatosGeneralesRequestDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.actualizarDatosGenerales(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/observacion")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionObservacion(@PathVariable Long id,
                                                                         @Valid @RequestBody FichaSeccionObservacionRequestDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionObservacion(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/observacion")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionObservacion(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionObservacion(id));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/psicoanamnesis")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionPsicoanamnesis(@PathVariable Long id,
                                                                            @Valid @RequestBody FichaSeccionPsicoanamnesisRequestDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionPsicoanamnesis(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/psicoanamnesis")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionPsicoanamnesis(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionPsicoanamnesis(id));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/condicion")
    public ResponseEntity<FichaPsicologicaDTO> actualizarCondicion(@PathVariable Long id,
                                                                    @Valid @RequestBody FichaCondicionRequestDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.actualizarCondicion(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/condicion")
    public ResponseEntity<FichaPsicologicaDTO> limpiarCondicion(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.limpiarCondicionClinica(id));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<FichaPsicologicaDTO> actualizarEstado(@PathVariable Long id,
                                                                 @RequestBody Map<String, String> body) {
        String estado = body != null ? body.get("estado") : null;
        return ResponseEntity.ok(fichaPsicologicaService.actualizarEstado(id, estado));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<FichaPsicologicaDTO> finalizarFicha(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.finalizarFicha(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFicha(@PathVariable Long id) {
        fichaPsicologicaService.eliminarFicha(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // NUEVOS ENDPOINTS PARA LAS SECCIONES ADICIONALES
    // ============================================

    // SECCIÓN 6: Adolescencia, Juventud y Adultez
    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/adolescencia")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionAdolescencia(@PathVariable Long id,
                                                                          @Valid @RequestBody AdolescenciaJuventudAdultezDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionAdolescencia(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/adolescencia")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionAdolescencia(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionAdolescencia(id));
    }

    // SECCIÓN 7: Psicoanamnesis Familiar
    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/familiar")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionFamiliar(@PathVariable Long id,
                                                                      @Valid @RequestBody PsicoanamnesisFamiliarDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionFamiliar(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/familiar")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionFamiliar(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionFamiliar(id));
    }

    // SECCIÓN 8: Exámenes de Funciones Psicológicas
    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/funciones-psicologicas")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionFuncionesPsicologicas(@PathVariable Long id,
                                                                                  @Valid @RequestBody ExamenFuncionesPsicologicasDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionFuncionesPsicologicas(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/funciones-psicologicas")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionFuncionesPsicologicas(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionFuncionesPsicologicas(id));
    }

    // SECCIONES 10 y 11: Rasgos de Personalidad y Exámenes Psicológicos
    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/rasgos-examenes")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionRasgosExamenes(@PathVariable Long id,
                                                                            @Valid @RequestBody RasgosPersonalidadExamenesDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionRasgosExamenes(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/rasgos-examenes")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionRasgosExamenes(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionRasgosExamenes(id));
    }

    // SECCIONES 12 y 13: Formulación Etiopatogénica y Pronóstico
    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/etiopatogenica-pronostico")
    public ResponseEntity<FichaPsicologicaDTO> guardarSeccionEtiopatogenicaPronostico(@PathVariable Long id,
                                                                                     @Valid @RequestBody FormulacionEtiopatogenicaPronosticoDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.guardarSeccionEtiopatogenicaPronostico(id, request));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}/etiopatogenica-pronostico")
    public ResponseEntity<FichaPsicologicaDTO> eliminarSeccionEtiopatogenicaPronostico(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.eliminarSeccionEtiopatogenicaPronostico(id));
    }

    // ============================================
    // ENDPOINTS UTILITARIOS PARA MÚLTIPLES SECCIONES
    // ============================================

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PutMapping("/{id}/todas-secciones-nuevas")
    public ResponseEntity<FichaPsicologicaDTO> actualizarTodasSeccionesNuevas(@PathVariable Long id,
                                                                              @Valid @RequestBody FichaSeccionesNuevasRequestDTO request) {
        return ResponseEntity.ok(fichaPsicologicaService.actualizarTodasSeccionesNuevas(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/{id}/completa")
    public ResponseEntity<FichaPsicologicaCompletaDTO> obtenerFichaCompleta(@PathVariable Long id) {
        return ResponseEntity.ok(fichaPsicologicaService.obtenerFichaCompletaPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/{id}/verificar-completitud")
    public ResponseEntity<Map<String, Boolean>> verificarSeccionesCompletas(@PathVariable Long id) {
        boolean completas = fichaPsicologicaService.verificarSeccionesNuevasCompletas(id);
        return ResponseEntity.ok(Map.of("completas", completas));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PostMapping("/completa")
    public ResponseEntity<FichaPsicologicaCompletaDTO> crearFichaCompleta(@Valid @RequestBody FichaPsicologicaCompletaRequestDTO request) {
        FichaPsicologicaCompletaDTO creada = fichaPsicologicaService.crearFichaCompleta(request);
        return ResponseEntity.created(URI.create("/api/fichas-psicologicas/" + creada.getId())).body(creada);
    }

}