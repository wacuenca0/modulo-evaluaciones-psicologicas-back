package ec.mil.dsndft.servicio_catalogos.repository;

import ec.mil.dsndft.servicio_catalogos.entity.PasswordChangeRequest;
import ec.mil.dsndft.servicio_catalogos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordChangeRequestRepository extends JpaRepository<PasswordChangeRequest, Long> {
    boolean existsByUsuario_IdAndStatus(Long usuarioId, PasswordChangeRequest.RequestStatus status);
    List<PasswordChangeRequest> findByStatus(PasswordChangeRequest.RequestStatus status);
}
