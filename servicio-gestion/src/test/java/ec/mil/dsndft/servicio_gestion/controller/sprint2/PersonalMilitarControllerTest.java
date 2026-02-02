package ec.mil.dsndft.servicio_gestion.controller.sprint2;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.mil.dsndft.servicio_gestion.controller.PersonalMilitarController;
import ec.mil.dsndft.servicio_gestion.model.dto.PersonalMilitarDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.PersonalMilitarUpsertRequestDTO;
import ec.mil.dsndft.servicio_gestion.service.PersonalMilitarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias Sprint 2
 * Estas pruebas corresponden al segundo incremento del proyecto (Sprint 2).
 * Para diferenciar, el nombre de la clase y los métodos incluye 'Sprint2'.
 * Si agregas pruebas de otros sprints, usa una clase diferente y documenta el sprint.
 */
@WebMvcTest(PersonalMilitarController.class)
@Import(ec.mil.dsndft.servicio_gestion.config.TestSecurityConfig.class)
class PersonalMilitarControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PersonalMilitarService personalMilitarService;

	@MockBean
	private ec.mil.dsndft.servicio_gestion.config.JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	private ec.mil.dsndft.servicio_gestion.config.JwtService jwtService;

	@Test
	@DisplayName("Debe listar personal militar (rol autorizado)")
	@WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
	void listarPersonal_debeRetornarLista() throws Exception {
		PersonalMilitarDTO dto = crearPersonalMilitarDTO();
		Mockito.when(personalMilitarService.findAll()).thenReturn(List.of(dto));
		mockMvc.perform(get("/api/personal-militar"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Debe obtener personal por ID (rol autorizado)")
	@WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
	void obtenerPersonal_debeRetornarPersonal() throws Exception {
		PersonalMilitarDTO dto = crearPersonalMilitarDTO();
		Mockito.when(personalMilitarService.findById(1L)).thenReturn(dto);
		mockMvc.perform(get("/api/personal-militar/1"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Debe registrar personal (rol PSICOLOGO)")
	@WithMockUser(roles = {"PSICOLOGO"})
	void registrarPersonal_debeCrearPersonal() throws Exception {
		PersonalMilitarUpsertRequestDTO req = new PersonalMilitarUpsertRequestDTO();
		req.setApellidosNombres("Juan Perez");
		PersonalMilitarDTO dto = crearPersonalMilitarDTO();
		Mockito.when(personalMilitarService.create(any())).thenReturn(dto);
		mockMvc.perform(post("/api/personal-militar")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(result -> {
					int status = result.getResponse().getStatus();
					if (status != 201 && status != 200) {
						throw new AssertionError("Expected status 201 or 200 but got " + status);
					}
				});
	}

	@Test
	@DisplayName("Debe actualizar personal (rol PSICOLOGO)")
	@WithMockUser(roles = {"PSICOLOGO"})
	void actualizarPersonal_debeActualizarPersonal() throws Exception {
		PersonalMilitarUpsertRequestDTO req = new PersonalMilitarUpsertRequestDTO();
		req.setApellidosNombres("Juan Perez");
		PersonalMilitarDTO dto = crearPersonalMilitarDTO();
		Mockito.when(personalMilitarService.update(eq(1L), any())).thenReturn(dto);
		mockMvc.perform(put("/api/personal-militar/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}

	// Utilidad para inicializar todos los campos relevantes del DTO
	private PersonalMilitarDTO crearPersonalMilitarDTO() {
		PersonalMilitarDTO dto = new PersonalMilitarDTO();
		dto.setId(1L);
		dto.setCedula("1234567890");
		dto.setApellidosNombres("Juan Perez");
		dto.setTipoPersona("MILITAR");
		dto.setEsMilitar(true);
		dto.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1));
		dto.setEdad(30);
		dto.setSexo("M");
		dto.setEtnia("Mestizo");
		dto.setEstadoCivil("Soltero");
		dto.setNroHijos(0);
		dto.setOcupacion("Oficial");
		dto.setServicioActivo(true);
		dto.setServicioPasivo(false);
		dto.setSeguro("ISSFA");
		dto.setGrado("Teniente");
		dto.setEspecialidad("Infantería");
		dto.setUnidadMilitar("Batallón XYZ");
		dto.setProvincia("Pichincha");
		dto.setCanton("Quito");
		dto.setParroquia("Centro");
		dto.setBarrioSector("La Mariscal");
		dto.setTelefono("022345678");
		dto.setCelular("0999999999");
		dto.setEmail("juan.perez@ejemplo.com");
		dto.setActivo(true);
		return dto;
	}
}
