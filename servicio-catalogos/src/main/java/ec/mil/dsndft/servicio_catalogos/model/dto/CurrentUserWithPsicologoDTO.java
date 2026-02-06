package ec.mil.dsndft.servicio_catalogos.model.dto;

import ec.mil.dsndft.servicio_catalogos.model.integration.PsicologoResponse;

public class CurrentUserWithPsicologoDTO {

    private UserDTO user;
    private PsicologoResponse psicologo;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public PsicologoResponse getPsicologo() {
        return psicologo;
    }

    public void setPsicologo(PsicologoResponse psicologo) {
        this.psicologo = psicologo;
    }
}
