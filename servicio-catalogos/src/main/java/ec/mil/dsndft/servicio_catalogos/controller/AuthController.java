package ec.mil.dsndft.servicio_catalogos.controller;

import ec.mil.dsndft.servicio_catalogos.model.dto.LoginRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UserDTO;
import ec.mil.dsndft.servicio_catalogos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login, usuario actual y cierre de sesión")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
               description = "Autentica al usuario y devuelve un token JWT para futuras peticiones")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String token = authService.login(loginRequestDTO);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current-user")
    @Operation(summary = "Obtener usuario actual",
               description = "Devuelve la información del usuario autenticado a partir del token JWT")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO userDTO = authService.getCurrentUser();
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Endpoint para cerrar sesión. El frontend debe borrar el token JWT.
     * Opcionalmente, aquí podrías invalidar el token si implementas blacklist.
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión",
               description = "Endpoint informativo; el frontend debe eliminar el token JWT almacenado")
    public ResponseEntity<?> logout() {
        // No es necesario hacer nada en el backend si no hay blacklist.
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }
}