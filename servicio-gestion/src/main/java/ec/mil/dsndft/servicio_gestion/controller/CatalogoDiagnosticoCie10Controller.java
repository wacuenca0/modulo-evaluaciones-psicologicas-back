package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10CreateRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10DTO;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10UpdateRequestDTO;
import ec.mil.dsndft.servicio_gestion.service.CatalogoDiagnosticoCie10Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@RestController
@RequestMapping("/api/catalogos/cie10")
@RequiredArgsConstructor
@Tag(name = "Catálogo CIE10", description = "Gestión de diagnósticos CIE10")
public class CatalogoDiagnosticoCie10Controller {

    private final CatalogoDiagnosticoCie10Service catalogoDiagnosticoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO','OBSERVADOR')")
    @GetMapping
    @Operation(summary = "Listar diagnósticos CIE10",
               description = "Obtiene diagnósticos CIE10 activos con búsqueda opcional y paginación")
    public ResponseEntity<org.springframework.data.domain.Page<CatalogoDiagnosticoCie10DTO>> listarActivos(
            @RequestParam(value = "q", required = false) String terminoBusqueda,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(catalogoDiagnosticoService.listarActivos(terminoBusqueda, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
    @GetMapping("/{id}")
    @Operation(summary = "Obtener diagnóstico por ID",
               description = "Recupera un diagnóstico CIE10 específico por su identificador")
    public ResponseEntity<CatalogoDiagnosticoCie10DTO> obtenerPorId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(catalogoDiagnosticoService.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    @Operation(summary = "Crear diagnóstico CIE10",
               description = "Registra un nuevo diagnóstico en el catálogo CIE10")
    public ResponseEntity<CatalogoDiagnosticoCie10DTO> crear(@Valid @RequestBody CatalogoDiagnosticoCie10CreateRequestDTO request) {
        CatalogoDiagnosticoCie10DTO creado = catalogoDiagnosticoService.crear(request);
        return ResponseEntity.created(URI.create("/api/catalogos/cie10/" + creado.getId())).body(creado);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar diagnóstico CIE10",
               description = "Modifica la información de un diagnóstico CIE10 existente")
    public ResponseEntity<CatalogoDiagnosticoCie10DTO> actualizar(@PathVariable @Min(1) Long id,
                                                                   @Valid @RequestBody CatalogoDiagnosticoCie10UpdateRequestDTO request) {
        return ResponseEntity.ok(catalogoDiagnosticoService.actualizar(id, request));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar diagnóstico CIE10",
               description = "Elimina lógicamente un diagnóstico del catálogo CIE10")
    public ResponseEntity<Void> eliminar(@PathVariable @Min(1) Long id) {
        catalogoDiagnosticoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
