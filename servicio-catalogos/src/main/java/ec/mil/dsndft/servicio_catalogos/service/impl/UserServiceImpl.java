package ec.mil.dsndft.servicio_catalogos.service.impl;

import ec.mil.dsndft.servicio_catalogos.client.PsicologoClient;
import ec.mil.dsndft.servicio_catalogos.model.dto.CreateUserRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UpdateUserRequestDTO;
import ec.mil.dsndft.servicio_catalogos.model.dto.UserDTO;
import ec.mil.dsndft.servicio_catalogos.entity.Role;
import ec.mil.dsndft.servicio_catalogos.entity.Usuario;
import ec.mil.dsndft.servicio_catalogos.model.integration.PsicologoCreateRequest;
import ec.mil.dsndft.servicio_catalogos.model.integration.PsicologoResponse;
import ec.mil.dsndft.servicio_catalogos.model.mapper.UserMapper;
import ec.mil.dsndft.servicio_catalogos.repository.RoleRepository;
import ec.mil.dsndft.servicio_catalogos.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;
import ec.mil.dsndft.servicio_catalogos.service.UserService;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PsicologoClient psicologoClient;

    public UserServiceImpl(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, PsicologoClient psicologoClient) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.psicologoClient = psicologoClient;
    }


    @Override
    public UserDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        // 1. Validar si ya existe usuario con el mismo username (ignorando mayúsculas/minúsculas)
        String usernameTrim = createUserRequestDTO.getUsername() != null ? createUserRequestDTO.getUsername().trim() : null;
        if (usernameTrim == null || usernameTrim.isEmpty()) {
            log.error("Intento de crear usuario sin username. Payload: {}", createUserRequestDTO);
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        usuarioRepository.findByUsernameIgnoreCase(usernameTrim)
            .ifPresent(u -> {
                log.warn("Intento de crear usuario duplicado: {}", usernameTrim);
                throw new DataIntegrityViolationException("Ya existe un usuario con ese nombre de usuario");
            });

        // 2. Resolver rol y validar datos del psicólogo SOLO si el rol requiere psicólogo
        Role role = roleRepository.findById(createUserRequestDTO.getRoleId())
            .filter(Role::getActivo)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado o inactivo"));

        boolean requierePsicologo = role.getNombre() != null && role.getNombre().equalsIgnoreCase("Psicologo");
        CreateUserRequestDTO.PsicologoData psicologoData = createUserRequestDTO.getPsicologo();
        if (requierePsicologo) {
            if (psicologoData == null) {
                log.error("Intento de crear usuario con rol Psicologo sin datos de psicólogo. Payload: {}", createUserRequestDTO);
                throw new IllegalArgumentException("Debe proporcionar los datos del psicólogo asociado al usuario");
            }
            // Validaciones de campos obligatorios y formato
            validatePsicologoData(psicologoData);
        }

        // 3. Verificar si ya existe un psicólogo con la misma cédula (solo si aplica)
        if (requierePsicologo) {
            try {
                PsicologoResponse existingPsicologo = psicologoClient.buscarPorCedula(psicologoData.getCedula().trim());
                if (existingPsicologo != null && Boolean.TRUE.equals(existingPsicologo.getActivo())) {
                    log.warn("Intento de crear psicólogo duplicado con cédula: {}", psicologoData.getCedula());
                    throw new DataIntegrityViolationException("Ya existe un psicólogo activo con la cédula: " + psicologoData.getCedula());
                }
            } catch (HttpClientErrorException.NotFound ex) {
                log.info("No existe psicólogo previo con cédula: {}. Se puede crear.", psicologoData.getCedula());
            } catch (RestClientException ex) {
                log.error("Error al verificar existencia de psicólogo por cédula {}: {}", psicologoData.getCedula(), ex.getMessage(), ex);
                throw new IllegalStateException("No se pudo verificar la existencia del psicólogo por un error de red");
            }
        }

        // 4. Crear usuario y (opcionalmente) psicólogo solo si todas las validaciones pasan

        Usuario usuario = new Usuario();
        usuario.setUsername(createUserRequestDTO.getUsername().trim().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(createUserRequestDTO.getPassword()));
        usuario.setEmail(createUserRequestDTO.getEmail() != null ? createUserRequestDTO.getEmail().trim() : null);
        usuario.setRole(role);
        usuario.setActivo(true);
        usuario.setBloqueado(false);
        usuario.setIntentosLogin(0);
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario creado correctamente en base de datos: {} (ID: {})", usuario.getUsername(), usuario.getId());

        // 5. Crear psicólogo en el servicio externo SOLO si el rol es Psicologo
        if (requierePsicologo) {
            PsicologoCreateRequest psicologoRequest = new PsicologoCreateRequest();
            psicologoRequest.setCedula(psicologoData.getCedula().trim());
            psicologoRequest.setNombres(psicologoData.getNombres().trim());
            psicologoRequest.setApellidos(psicologoData.getApellidos().trim());
            psicologoRequest.setApellidosNombres((psicologoData.getApellidos().trim() + " " + psicologoData.getNombres().trim()).trim());
            psicologoRequest.setUsername(usuario.getUsername());
            psicologoRequest.setEmail(createUserRequestDTO.getEmail());
            psicologoRequest.setUsuarioId(usuario.getId());
            psicologoRequest.setTelefono(psicologoData.getTelefono());
            psicologoRequest.setCelular(psicologoData.getCelular());
            psicologoRequest.setGrado(psicologoData.getGrado());
            psicologoRequest.setUnidadMilitar(psicologoData.getUnidadMilitar());
            psicologoRequest.setEspecialidad(psicologoData.getEspecialidad());
            psicologoRequest.setActivo(Boolean.TRUE);

            try {
                PsicologoResponse psicologoCreado = psicologoClient.crearPsicologo(psicologoRequest);
                if (psicologoCreado == null || psicologoCreado.getId() == null) {
                    log.error("No se pudo crear el psicólogo en el microservicio de gestión. Payload: {}", psicologoRequest);
                    // rollback explícito del usuario creado para no dejar inconsistencias
                    safeDeleteUsuario(usuario.getId());
                    throw new IllegalStateException("No se pudo crear el psicólogo en el microservicio de gestión");
                }
                log.info("Psicólogo creado correctamente en gestión: {} (ID: {})", psicologoCreado.getCedula(), psicologoCreado.getId());
            } catch (HttpClientErrorException ex) {
                log.error("Error HTTP al crear psicólogo para usuario {}. Se eliminará el usuario creado.", usuario.getUsername(), ex);
                safeDeleteUsuario(usuario.getId());
                if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                    // Propagar como violación de integridad para que llegue como 409 al frontend
                    throw new DataIntegrityViolationException("No se pudo crear el psicólogo asociado al usuario: " + ex.getResponseBodyAsString(), ex);
                }
                throw new IllegalStateException("No se pudo crear el psicólogo asociado al usuario por un error en el microservicio de gestión", ex);
            } catch (RestClientException ex) {
                log.error("Error de comunicación al crear psicólogo para usuario {}. Se eliminará el usuario creado.", usuario.getUsername(), ex);
                safeDeleteUsuario(usuario.getId());
                throw new IllegalStateException("No se pudo crear el psicólogo asociado al usuario por un problema de comunicación con el microservicio de gestión", ex);
            }
        } else {
            log.info("Rol '{}' no requiere creación de psicólogo. Usuario ID: {}", role.getNombre(), usuario.getId());
        }

        return userMapper.toDTO(usuario);
    }

    private void validatePsicologoData(CreateUserRequestDTO.PsicologoData psicologoData) {
        if (psicologoData.getCedula() == null || psicologoData.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula del psicólogo es obligatoria");
        }
        if (psicologoData.getNombres() == null || psicologoData.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres del psicólogo son obligatorios");
        }
        if (psicologoData.getApellidos() == null || psicologoData.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos del psicólogo son obligatorios");
        }
        // Validar formato de cédula (ejemplo para Ecuador)
        if (!psicologoData.getCedula().trim().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("La cédula debe tener 10 dígitos numéricos");
        }
    }

    private void safeDeleteUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return;
        }
        try {
            log.info("Eliminando usuario {} debido a fallo en la creación de psicólogo asociado", usuarioId);
            usuarioRepository.deleteById(usuarioId);
        } catch (Exception ex) {
            log.error("Error al eliminar usuario {} durante el rollback de creación de psicólogo", usuarioId, ex);
        }
    }

    @Override
    public UserDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        Usuario usuario = usuarioRepository.findByUsername(updateUserRequestDTO.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (updateUserRequestDTO.getEmail() != null) {
            usuario.setEmail(updateUserRequestDTO.getEmail());
        }
        if (updateUserRequestDTO.getActive() != null) {
            usuario.setActivo(updateUserRequestDTO.getActive());
        }

        if (updateUserRequestDTO.getRoleId() != null) {
            Role role = roleRepository.findById(updateUserRequestDTO.getRoleId())
                .filter(Role::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado o inactivo"));
            usuario.setRole(role);
        }

        return userMapper.toDTO(usuario);
    }

    @Override
    public void deleteUser(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return usuarioRepository.findAll().stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return userMapper.toDTO(usuario);
    }
}