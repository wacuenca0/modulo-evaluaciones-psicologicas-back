package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaResponseDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionSeguimientoRequestDTO;
import ec.mil.dsndft.servicio_gestion.service.AtencionPsicologicaService;
import ec.mil.dsndft.servicio_gestion.repository.AtencionPsicologicaHistorialRepository;
import ec.mil.dsndft.servicio_gestion.entity.AtencionPsicologicaHistorial;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaHistorialDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.ReprogramarAtencionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/atenciones")
@RequiredArgsConstructor
@ConditionalOnBean(AtencionPsicologicaService.class)
@Tag(name = "Atenciones psicológicas", description = "Gestión de atenciones y su historial")
public class AtencionPsicologicaController {
    private final AtencionPsicologicaService atencionService;
    private final AtencionPsicologicaHistorialRepository historialRepository;
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de una atención",
               description = "Devuelve la lista de cambios de estado para una atención psicológica")
    public ResponseEntity<List<AtencionPsicologicaHistorialDTO>> obtenerHistorialAtencion(@PathVariable Long id) {
        List<AtencionPsicologicaHistorial> historial = historialRepository.findByAtencionIdOrderByFechaCambioAsc(id);
        List<AtencionPsicologicaHistorialDTO> dtoList = historial.stream().map(h -> {
            AtencionPsicologicaHistorialDTO dto = new AtencionPsicologicaHistorialDTO();
            dto.setId(h.getId());
            dto.setEstado(h.getEstado());
            dto.setRazonCambio(h.getRazonCambio());
            dto.setFechaCambio(h.getFechaCambio());
            if (h.getPsicologo() != null) {
                dto.setPsicologoId(h.getPsicologo().getId());
                dto.setPsicologoNombres(h.getPsicologo().getNombres());
                dto.setPsicologoApellidos(h.getPsicologo().getApellidos());
            }
            return dto;
        }).toList();
        return ResponseEntity.ok(dtoList);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @PostMapping
    @Operation(summary = "Crear atención",
               description = "Registra una nueva atención psicológica para un paciente")
    public ResponseEntity<AtencionPsicologicaResponseDTO> crearAtencion(@RequestBody AtencionPsicologicaRequestDTO request) {
        AtencionPsicologicaResponseDTO creado = atencionService.crearAtencion(request);
        return ResponseEntity.created(URI.create("/api/atenciones/" + creado.getId())).body(creado);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/{id}")
    @Operation(summary = "Obtener atención por ID",
               description = "Obtiene el detalle de una atención psicológica por su identificador")
    public ResponseEntity<AtencionPsicologicaResponseDTO> obtenerAtencion(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.obtenerPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar atención",
               description = "Actualiza los datos clínicos y administrativos de una atención")
    public ResponseEntity<AtencionPsicologicaResponseDTO> actualizarAtencion(
            @PathVariable Long id, 
            @RequestBody AtencionPsicologicaRequestDTO request) {
        return ResponseEntity.ok(atencionService.actualizarAtencion(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar atención",
               description = "Marca una atención como inactiva")
    public ResponseEntity<Void> eliminarAtencion(@PathVariable Long id) {
        atencionService.eliminarAtencion(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/psicologo/{psicologoId}")
    @Operation(summary = "Listar atenciones por psicólogo",
               description = "Obtiene atenciones paginadas asociadas a un psicólogo")

    public ResponseEntity<ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO> listarPorPsicologo(@PathVariable Long psicologoId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<AtencionPsicologicaResponseDTO> result = atencionService.listarPorPsicologo(psicologoId, pageable);
        java.util.List<AtencionPsicologicaResponseDTO> content = result.getContent();
        ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO dto = new ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO(
            content,
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
    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Listar atenciones por paciente",
               description = "Obtiene atenciones paginadas para un paciente")

    public ResponseEntity<ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO> listarPorPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<AtencionPsicologicaResponseDTO> result = atencionService.listarPorPaciente(pacienteId, pageable);
        java.util.List<AtencionPsicologicaResponseDTO> content = result.getContent();
        ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO dto = new ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO(
            content,
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
    @GetMapping("/filtro-atencion")
    @Operation(summary = "Listar atenciones por filtros",
               description = "Filtra atenciones por estado, nombre y fecha")
    public ResponseEntity<ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO> listarPorFiltroAtencion(
            @RequestParam(required = false) String estadoAtencion,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<AtencionPsicologicaResponseDTO> result = atencionService.listarPorFiltroAtencion(estadoAtencion, nombre, fecha, pageable);
        java.util.List<AtencionPsicologicaResponseDTO> content = result.getContent();
        ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO dto = new ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaPageSafeDTO(
            content,
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
    @PostMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar atención",
               description = "Registra el cierre clínico de una atención")
    public ResponseEntity<AtencionPsicologicaResponseDTO> finalizarAtencion(
            @PathVariable Long id, 
            @RequestBody AtencionPsicologicaRequestDTO request) {
        return ResponseEntity.ok(atencionService.finalizarAtencion(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar atención",
               description = "Cancela una atención y registra la razón")
    public ResponseEntity<AtencionPsicologicaResponseDTO> cancelarAtencion(
            @PathVariable Long id, 
            @RequestParam String razon) {
        return ResponseEntity.ok(atencionService.cancelarAtencion(id, razon));
    }

    // NUEVO ENDPOINT: Crear atención de seguimiento solo para fichas en seguimiento
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @PostMapping("/seguimiento")
    @Operation(summary = "Crear atención de seguimiento",
               description = "Crea una nueva atención de seguimiento para una ficha en seguimiento")
    public ResponseEntity<AtencionPsicologicaResponseDTO> crearAtencionSeguimiento(@RequestBody AtencionSeguimientoRequestDTO request) {
        return ResponseEntity.ok(atencionService.crearAtencionSeguimiento(request));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @PatchMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar atención",
               description = "Modifica fecha y hora de una atención manteniendo su motivo original")
    public ResponseEntity<AtencionPsicologicaResponseDTO> reprogramarAtencion(
            @PathVariable Long id,
            @RequestBody ReprogramarAtencionRequest request) {
        AtencionPsicologicaResponseDTO atencionReprogramada = atencionService.reprogramarAtencion(id, request);
        return ResponseEntity.ok(atencionReprogramada);
    }
}