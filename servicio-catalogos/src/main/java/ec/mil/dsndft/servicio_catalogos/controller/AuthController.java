package ec.mil.dsndft.servicio_catalogos.controller;

import ec.mil.dsndft.servicio_catalogos.model.dto.LoginRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UserDTO;
import ec.mil.dsndft.servicio_catalogos.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String token = authService.login(loginRequestDTO);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO userDTO = authService.getCurrentUser();
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Endpoint para cerrar sesión. El frontend debe borrar el token JWT.
     * Opcionalmente, aquí podrías invalidar el token si implementas blacklist.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // No es necesario hacer nada en el backend si no hay blacklist.
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }
}