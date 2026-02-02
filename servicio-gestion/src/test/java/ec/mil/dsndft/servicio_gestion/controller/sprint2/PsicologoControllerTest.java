package ec.mil.dsndft.servicio_gestion.controller.sprint2;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.mil.dsndft.servicio_gestion.controller.PsicologoController;
import ec.mil.dsndft.servicio_gestion.model.dto.PsicologoDTO;
import ec.mil.dsndft.servicio_gestion.service.PsicologoService;
import ec.mil.dsndft.servicio_gestion.service.support.AuthenticatedPsicologoProvider;
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

@WebMvcTest(PsicologoController.class)
@Import(ec.mil.dsndft.servicio_gestion.config.TestSecurityConfig.class)
class PsicologoControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PsicologoService psicologoService;
	@MockBean
	private AuthenticatedPsicologoProvider authenticatedPsicologoProvider;

	@MockBean
	private ec.mil.dsndft.servicio_gestion.config.JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	private ec.mil.dsndft.servicio_gestion.config.JwtService jwtService;

	@Test
	@DisplayName("Debe listar psicólogos (rol autorizado)")
	@WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
	void listarPsicologos_debeRetornarLista() throws Exception {
		PsicologoDTO dto = new PsicologoDTO();
		dto.setId(1L); dto.setNombres("Ana"); dto.setApellidosNombres("Ana");
		Mockito.when(psicologoService.findAll()).thenReturn(List.of(dto));
		mockMvc.perform(get("/api/psicologos"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Debe obtener psicólogo por ID (rol autorizado)")
	@WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
	void obtenerPsicologo_debeRetornarPsicologo() throws Exception {
		PsicologoDTO dto = new PsicologoDTO();
		dto.setId(1L); dto.setNombres("Ana"); dto.setApellidosNombres("Ana");
		Mockito.when(psicologoService.findById(1L)).thenReturn(dto);
		mockMvc.perform(get("/api/psicologos/1"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Debe crear psicólogo (rol ADMINISTRADOR)")
	@WithMockUser(roles = {"ADMINISTRADOR"})
	void crearPsicologo_debeCrearPsicologo() throws Exception {
		PsicologoDTO req = new PsicologoDTO();
		req.setNombres("Ana"); req.setApellidosNombres("Ana");
		PsicologoDTO dto = new PsicologoDTO();
		dto.setId(1L); dto.setNombres("Ana"); dto.setApellidosNombres("Ana");
		Mockito.when(psicologoService.save(any())).thenReturn(dto);
		mockMvc.perform(post("/api/psicologos")
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
	@DisplayName("Debe actualizar psicólogo (rol ADMINISTRADOR)")
	@WithMockUser(roles = {"ADMINISTRADOR"})
	void actualizarPsicologo_debeActualizarPsicologo() throws Exception {
		PsicologoDTO req = new PsicologoDTO();
		req.setNombres("Ana"); req.setApellidosNombres("Ana");
		PsicologoDTO dto = new PsicologoDTO();
		dto.setId(1L); dto.setNombres("Ana"); dto.setApellidosNombres("Ana");
		Mockito.when(psicologoService.save(any())).thenReturn(dto);
		mockMvc.perform(put("/api/psicologos/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}
}
