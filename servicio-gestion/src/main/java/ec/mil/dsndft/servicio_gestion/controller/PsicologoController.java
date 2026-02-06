package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.model.dto.PsicologoDTO;
import ec.mil.dsndft.servicio_gestion.service.PsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
// Si el context-path está activado como '/gestion', el endpoint será '/gestion/api/psicologos'.
@RequestMapping("/api/psicologos")
@RequiredArgsConstructor
@Tag(name = "Psicólogos", description = "Operaciones para gestión de psicólogos")
public class PsicologoController {
	@PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO','OBSERVADOR')")
	@Operation(summary = "Obtener nombre del psicólogo autenticado",
	           description = "Devuelve el nombre completo del psicólogo asociado al usuario actual")
	@GetMapping("/nombre-por-usuario")
	public ResponseEntity<String> obtenerNombrePorUsuarioId() {
		// Obtener el usuarioId del token JWT usando el proveedor de autenticación
		Long usuarioId = authenticatedPsicologoProvider.getCurrentUserId();
		String nombre = psicologoService.obtenerNombrePorUsuarioId(usuarioId);
		return ResponseEntity.ok(nombre);
	}

	private final PsicologoService psicologoService;
	private final ec.mil.dsndft.servicio_gestion.service.support.AuthenticatedPsicologoProvider authenticatedPsicologoProvider;

	@PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
	@Operation(summary = "Listar psicólogos activos",
	           description = "Devuelve el listado de todos los psicólogos activos")
	@GetMapping
	public ResponseEntity<List<PsicologoDTO>> listarPsicologos() {
		return ResponseEntity.ok(psicologoService.findAll());
	}

	@PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
	@Operation(summary = "Obtener psicólogo por usuario",
	           description = "Obtiene la información del psicólogo asociado a un usuario dado su ID de usuario")
	@GetMapping("/por-usuario/{usuarioId}")
	public ResponseEntity<PsicologoDTO> obtenerPorUsuarioId(@PathVariable @Parameter(description = "ID del usuario") Long usuarioId) {
		PsicologoDTO dto = psicologoService.findByUsuarioId(usuarioId);
		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO')")
	@Operation(summary = "Obtener psicólogo por ID",
	           description = "Devuelve la información de un psicólogo por su identificador")
	@GetMapping("/{id:\\d+}")
	public ResponseEntity<PsicologoDTO> obtenerPsicologo(@PathVariable @Parameter(description = "ID del psicólogo") Long id) {
		return ResponseEntity.ok(psicologoService.findById(id));
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@Operation(summary = "Crear psicólogo",
	           description = "Crea un nuevo psicólogo a partir de los datos proporcionados")
	@PostMapping
	public ResponseEntity<PsicologoDTO> crearPsicologo(@RequestBody Object body) {
		// Log para depuración del tipo de body recibido
		System.out.println("[DEBUG] Body recibido en crearPsicologo: " + (body != null ? body.getClass() : "null"));
		PsicologoDTO dto;
		if (body instanceof PsicologoDTO) {
			dto = (PsicologoDTO) body;
		} else {
			// Intentar mapear desde PsicologoCreateRequest
			dto = mapPsicologoCreateRequestToDTO(body);
		}
		PsicologoDTO creado = psicologoService.save(dto);
		return ResponseEntity.created(URI.create("/api/psicologos/" + creado.getId())).body(creado);
	}

	// Método auxiliar para mapear PsicologoCreateRequest a PsicologoDTO
	private PsicologoDTO mapPsicologoCreateRequestToDTO(Object body) {
		// Conversión manual, asumiendo que el body es un LinkedHashMap (por Jackson)
		if (body instanceof java.util.Map) {
			java.util.Map<?,?> map = (java.util.Map<?,?>) body;
			PsicologoDTO dto = new PsicologoDTO();
			dto.setCedula((String) map.get("cedula"));
			dto.setNombres((String) map.get("nombres"));
			dto.setApellidos((String) map.get("apellidos"));
			dto.setApellidosNombres((String) map.get("apellidosNombres"));
			dto.setUsuarioId(map.get("usuarioId") != null ? Long.valueOf(map.get("usuarioId").toString()) : null);
			dto.setUsername((String) map.get("username"));
			dto.setEmail((String) map.get("email"));
			dto.setTelefono((String) map.get("telefono"));
			dto.setCelular((String) map.get("celular"));
			dto.setGrado((String) map.get("grado"));
			dto.setUnidadMilitar((String) map.get("unidadMilitar"));
			dto.setEspecialidad((String) map.get("especialidad"));
			dto.setActivo(map.get("activo") != null ? Boolean.valueOf(map.get("activo").toString()) : Boolean.TRUE);
			return dto;
		}
		throw new IllegalArgumentException("Tipo de cuerpo no soportado para crear psicólogo");
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@Operation(summary = "Actualizar psicólogo",
	           description = "Actualiza los datos de un psicólogo existente")
	@PutMapping("/{id:\\d+}")
	public ResponseEntity<PsicologoDTO> actualizarPsicologo(@PathVariable @Parameter(description = "ID del psicólogo") Long id,
	                                                      @RequestBody PsicologoDTO dto) {
		dto.setId(id);
		return ResponseEntity.ok(psicologoService.save(dto));
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@Operation(summary = "Eliminar psicólogo",
	           description = "Elimina (lógicamente) un psicólogo por su ID")
	@DeleteMapping("/{id:\\d+}")
	public ResponseEntity<Void> eliminarPsicologo(@PathVariable @Parameter(description = "ID del psicólogo") Long id) {
		psicologoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyRole('ADMINISTRADOR','PSICOLOGO','OBSERVADOR')")
	@Operation(summary = "Buscar psicólogos",
	           description = "Permite buscar psicólogos por texto libre o por cédula exacta")
	@GetMapping("/buscar")
	public ResponseEntity<List<PsicologoDTO>> buscar(
	        @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false)
	        @Parameter(description = "Texto a buscar en nombre, usuario o correo del psicólogo") String q,
	        @org.springframework.web.bind.annotation.RequestParam(name = "cedula", required = false)
	        @Parameter(description = "Cédula exacta del psicólogo") String cedula
	) {
		// Si se provee cédula, priorizar búsqueda exacta por cédula
		if (cedula != null && !cedula.isBlank()) {
			PsicologoDTO encontrado = psicologoService.findByCedula(cedula);
			return ResponseEntity.ok(encontrado != null ? java.util.List.of(encontrado) : java.util.List.of());
		}
		// Búsqueda básica en memoria por texto (nombres/apellidos/username/email)
		List<PsicologoDTO> todos = psicologoService.findAll();
		if (q == null || q.isBlank()) {
			return ResponseEntity.ok(todos);
		}
		String term = q.toLowerCase();
		List<PsicologoDTO> filtrados = todos.stream()
			.filter(p -> {
				String acumulado = String.join(" ",
					p.getApellidosNombres() != null ? p.getApellidosNombres() : "",
					p.getUsername() != null ? p.getUsername() : "",
					p.getEmail() != null ? p.getEmail() : ""
				).toLowerCase();
				return acumulado.contains(term);
			})
			.toList();
		return ResponseEntity.ok(filtrados);
	}
}