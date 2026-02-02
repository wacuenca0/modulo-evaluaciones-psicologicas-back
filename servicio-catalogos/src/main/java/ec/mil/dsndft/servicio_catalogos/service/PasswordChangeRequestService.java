package ec.mil.dsndft.servicio_catalogos.service;

import ec.mil.dsndft.servicio_catalogos.model.dto.PasswordChangeRequestDTO;
import java.util.List;

public interface PasswordChangeRequestService {
    PasswordChangeRequestDTO createRequest(String username, String motivo);
    List<PasswordChangeRequestDTO> getPendingRequests();
    PasswordChangeRequestDTO approveRequest(Long requestId, String newPassword, String adminUsername);
    PasswordChangeRequestDTO rejectRequest(Long requestId, String adminUsername, String adminNotes);

    List<PasswordChangeRequestDTO> getRequestsByStatus(String status);
}
