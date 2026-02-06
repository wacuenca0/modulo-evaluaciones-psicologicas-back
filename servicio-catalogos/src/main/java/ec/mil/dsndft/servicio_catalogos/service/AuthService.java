package ec.mil.dsndft.servicio_catalogos.service;

import ec.mil.dsndft.servicio_catalogos.model.dto.LoginRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UserDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.SelfPasswordChangeDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.CurrentUserWithPsicologoDTO;

public interface AuthService {
    String login(LoginRequestDTO loginRequestDTO);
    UserDTO getCurrentUser();
    void changeOwnPassword(SelfPasswordChangeDTO request);
    CurrentUserWithPsicologoDTO getCurrentUserWithPsicologo();
}