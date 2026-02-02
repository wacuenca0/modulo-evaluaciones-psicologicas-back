package ec.mil.dsndft.servicio_catalogos.controller;

import ec.mil.dsndft.servicio_catalogos.model.dto.PasswordChangeRequestDTO;
import ec.mil.dsndft.servicio_catalogos.service.PasswordChangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/password-change")
public class PasswordChangeRequestController {

    private final PasswordChangeRequestService passwordChangeService;

    public PasswordChangeRequestController(PasswordChangeRequestService passwordChangeService) {
        this.passwordChangeService = passwordChangeService;
    }

    // Cualquier usuario puede solicitar cambio de contraseña (libre, sin autenticación)
    @PostMapping("/request")
    public ResponseEntity<PasswordChangeRequestDTO> requestPasswordChange(@RequestParam String username, @RequestParam String motivo) {
        PasswordChangeRequestDTO response = passwordChangeService.createRequest(username, motivo);
        return ResponseEntity.ok(response);
    }

    // Solo administradores pueden ver solicitudes pendientes
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/pending")
    public ResponseEntity<List<PasswordChangeRequestDTO>> getPendingRequests() {
        List<PasswordChangeRequestDTO> requests = passwordChangeService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    // Listar solicitudes por estado (PENDIENTE, APROBADO, RECHAZADO)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("")
    public ResponseEntity<List<PasswordChangeRequestDTO>> getRequestsByStatus(@RequestParam String status) {
        List<PasswordChangeRequestDTO> requests = passwordChangeService.getRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    // Solo administradores pueden aprobar solicitudes
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<PasswordChangeRequestDTO> approveRequest(@PathVariable Long requestId, @RequestParam String newPassword, Authentication authentication) {
        String adminUsername = authentication.getName();
        PasswordChangeRequestDTO response = passwordChangeService.approveRequest(requestId, newPassword, adminUsername);
        return ResponseEntity.ok(response);
    }

    // Solo administradores pueden rechazar solicitudes
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<PasswordChangeRequestDTO> rejectRequest(@PathVariable Long requestId, @RequestParam(required = false) String reason, Authentication authentication) {
        String adminUsername = authentication.getName();
        PasswordChangeRequestDTO response = passwordChangeService.rejectRequest(requestId, adminUsername, reason);
        return ResponseEntity.ok(response);
    }
}
