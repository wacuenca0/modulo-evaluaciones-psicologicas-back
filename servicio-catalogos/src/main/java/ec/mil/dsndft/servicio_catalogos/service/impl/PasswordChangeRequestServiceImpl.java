
package ec.mil.dsndft.servicio_catalogos.service.impl;

import ec.mil.dsndft.servicio_catalogos.entity.PasswordChangeRequest;
import ec.mil.dsndft.servicio_catalogos.entity.Usuario;
import ec.mil.dsndft.servicio_catalogos.model.dto.PasswordChangeRequestDTO;
import ec.mil.dsndft.servicio_catalogos.repository.PasswordChangeRequestRepository;
import ec.mil.dsndft.servicio_catalogos.repository.UsuarioRepository;
import ec.mil.dsndft.servicio_catalogos.service.PasswordChangeRequestService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PasswordChangeRequestServiceImpl implements PasswordChangeRequestService {

    private final PasswordChangeRequestRepository requestRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordChangeRequestServiceImpl(PasswordChangeRequestRepository requestRepository,
                                            UsuarioRepository usuarioRepository,
                                            PasswordEncoder passwordEncoder) {
        this.requestRepository = requestRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PasswordChangeRequestDTO createRequest(String username, String motivo) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede ser nulo o vacío");
        }
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        if (usuario.getId() == null) {
            throw new IllegalStateException("El usuario recuperado no tiene ID (posible inconsistencia en la base de datos)");
        }

        boolean hasPendingRequest = requestRepository.existsByUsuario_IdAndStatus(
            usuario.getId(), PasswordChangeRequest.RequestStatus.PENDIENTE);
        if (hasPendingRequest) {
            throw new IllegalStateException("Ya existe una solicitud pendiente para este usuario");
        }

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setUsuario(usuario);
        request.setUsernameSnapshot(usuario.getUsername());
        request.setMotivo(motivo);

        if (request.getUsuario() == null || request.getUsuario().getId() == null) {
            throw new IllegalStateException("No se pudo asociar el usuario a la solicitud de cambio de contraseña");
        }

        // Log para depuración
        System.out.println("[PasswordChangeRequestServiceImpl] Creando solicitud para usuario_id=" + request.getUsuario().getId() + ", username=" + request.getUsuario().getUsername());

        PasswordChangeRequest saved = requestRepository.save(request);
        return convertToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordChangeRequestDTO> getPendingRequests() {
        return requestRepository.findByStatus(PasswordChangeRequest.RequestStatus.PENDIENTE)
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordChangeRequestDTO> getRequestsByStatus(String status) {
        PasswordChangeRequest.RequestStatus reqStatus;
        try {
            reqStatus = PasswordChangeRequest.RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado inválido: " + status + ". Debe ser PENDIENTE, APROBADO o RECHAZADO.");
        }
        return requestRepository.findByStatus(reqStatus)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public PasswordChangeRequestDTO approveRequest(Long requestId, String newPassword, String adminUsername) {
        PasswordChangeRequest request = requestRepository.findById(Long.valueOf(requestId))
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (request.getStatus() != PasswordChangeRequest.RequestStatus.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        Usuario usuario = request.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        request.setStatus(PasswordChangeRequest.RequestStatus.APROBADO);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(adminUsername);

        PasswordChangeRequest saved = requestRepository.save(request);
        return convertToDTO(saved);
    }

    @Override
    public PasswordChangeRequestDTO rejectRequest(Long requestId, String adminUsername, String adminNotes) {
        PasswordChangeRequest request = requestRepository.findById(Long.valueOf(requestId))
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (request.getStatus() != PasswordChangeRequest.RequestStatus.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        request.setStatus(PasswordChangeRequest.RequestStatus.RECHAZADO);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(adminUsername);
        request.setMotivo(request.getMotivo() + " - Rechazado: " + (adminNotes != null ? adminNotes : "Sin motivo"));

        PasswordChangeRequest saved = requestRepository.save(request);
        return convertToDTO(saved);
    }

    private PasswordChangeRequestDTO convertToDTO(PasswordChangeRequest request) {
        PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO();
        dto.setId(request.getId());
        dto.setUsername(request.getUsuario().getUsername());
        dto.setMotivo(request.getMotivo());
        dto.setStatus(request.getStatus().toString());
        dto.setRequestedAt(request.getRequestedAt());
        return dto;
    }
}
