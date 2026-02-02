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
    @Transactional
    public UserDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        // 1. Validar si ya existe usuario con el mismo username (ignorando mayúsculas/minúsculas)
        usuarioRepository.findByUsernameIgnoreCase(createUserRequestDTO.getUsername().trim())
            .ifPresent(u -> {
                throw new DataIntegrityViolationException("Ya existe un usuario con ese nombre de usuario");
            });

        // 2. Validar datos del psicólogo
        CreateUserRequestDTO.PsicologoData psicologoData = createUserRequestDTO.getPsicologo();
        if (psicologoData == null) {
            throw new IllegalArgumentException("Debe proporcionar los datos del psicólogo asociado al usuario");
        }
        // Validaciones de campos obligatorios y formato
        validatePsicologoData(psicologoData);

        // 3. Verificar si ya existe un psicólogo con la misma cédula
        try {
            PsicologoResponse existingPsicologo = psicologoClient.buscarPorCedula(psicologoData.getCedula().trim());
            if (existingPsicologo != null && existingPsicologo.getActivo() != null && existingPsicologo.getActivo()) {
                throw new DataIntegrityViolationException("Ya existe un psicólogo activo con la cédula: " + psicologoData.getCedula());
            }
        } catch (HttpClientErrorException.NotFound ex) {
            // Psicólogo no encontrado, está bien para continuar
        } catch (RestClientException ex) {
            // Error al verificar, continuar pero registrar log
            System.err.println("Advertencia: No se pudo verificar existencia de psicólogo: " + ex.getMessage());
        }

        // 4. Crear usuario
        Role role = roleRepository.findById(createUserRequestDTO.getRoleId())
            .filter(Role::getActivo)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado o inactivo"));

        Usuario usuario = new Usuario();
        usuario.setUsername(createUserRequestDTO.getUsername().trim().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(createUserRequestDTO.getPassword()));
        usuario.setEmail(createUserRequestDTO.getEmail() != null ? createUserRequestDTO.getEmail().trim() : null);
        usuario.setRole(role);
        usuario.setActivo(true);
        usuario.setBloqueado(false);
        usuario.setIntentosLogin(0);
        usuario = usuarioRepository.save(usuario);

        try {
            // 5. Crear psicólogo en el servicio externo
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

            psicologoClient.crearPsicologo(psicologoRequest);

        } catch (HttpClientErrorException.Conflict ex) {
            // Si hay conflicto al crear el psicólogo, revertir la creación del usuario
            usuarioRepository.delete(usuario);
            throw new DataIntegrityViolationException(
                "No se pudo crear el psicólogo debido a un conflicto de datos: " + ex.getMessage()
            );
        } catch (RestClientException ex) {
            // Si hay error en la comunicación, revertir todo
            usuarioRepository.delete(usuario);
            throw new IllegalStateException(
                "No se pudo registrar el psicólogo asociado al usuario. Operación revertida.",
                ex
            );
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
        if (!psicologoData.getCedula().trim().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("La cédula debe tener 10 dígitos numéricos");
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

        usuarioRepository.save(usuario);
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