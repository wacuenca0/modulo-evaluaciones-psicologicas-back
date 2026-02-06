package ec.mil.dsndft.servicio_catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dev")
@Tag(name = "Herramientas de desarrollo", description = "Utilidades técnicas para soporte y pruebas")
public class DevUtilsController {

    private final PasswordEncoder passwordEncoder;

    public DevUtilsController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Dev helper: genera el hash BCrypt para una contraseña dada
    @GetMapping("/hash")
    @Operation(summary = "Generar hash de contraseña",
               description = "Devuelve el hash BCrypt de una contraseña en texto plano (uso solo en entornos controlados)")
    public ResponseEntity<Map<String, String>> hash(@RequestParam("pwd") String pwd) {
        String hash = passwordEncoder.encode(pwd);
        return ResponseEntity.ok(Map.of("password", pwd, "bcryptHash", hash));
    }
}
