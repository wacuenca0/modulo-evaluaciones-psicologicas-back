package ec.mil.dsndft.servicio_catalogos.controller;

import ec.mil.dsndft.servicio_catalogos.model.dto.CreateUserRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UpdateUserRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UserDTO;
import ec.mil.dsndft.servicio_catalogos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones para gestión de usuarios de la plataforma")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear usuario",
               description = "Crea un nuevo usuario con el rol y, cuando aplique, el psicólogo asociado")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequestDTO createUserRequestDTO) {
        UserDTO userDTO = userService.createUser(createUserRequestDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar usuario",
               description = "Actualiza los datos de un usuario existente")
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        UserDTO userDTO = userService.updateUser(updateUserRequestDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario",
               description = "Elimina un usuario identificado por su username")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Parameter(description = "Nombre de usuario") String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar usuarios",
               description = "Obtiene el listado completo de usuarios registrados")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener usuario por ID",
               description = "Obtiene la información de un usuario por su identificador interno")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable @Parameter(description = "ID del usuario") Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}