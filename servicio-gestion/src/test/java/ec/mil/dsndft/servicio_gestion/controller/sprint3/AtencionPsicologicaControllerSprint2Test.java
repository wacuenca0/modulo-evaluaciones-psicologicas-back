
package ec.mil.dsndft.servicio_gestion.controller.sprint3;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mil.dsndft.servicio_gestion.controller.AtencionPsicologicaController;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaResponseDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionSeguimientoRequestDTO;
import ec.mil.dsndft.servicio_gestion.service.AtencionPsicologicaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias Sprint 2 - AtencionPsicologicaController
 */
@WebMvcTest(controllers = AtencionPsicologicaController.class, excludeFilters = {
    @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = ec.mil.dsndft.servicio_gestion.config.JwtAuthenticationFilter.class)
})
@AutoConfigureMockMvc
@Import(ec.mil.dsndft.servicio_gestion.config.TestSecurityConfig.class)
class AtencionPsicologicaControllerSprint2Test {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AtencionPsicologicaService atencionService;

    // Mock de seguridad para evitar errores de contexto
    @MockBean
    private ec.mil.dsndft.servicio_gestion.config.JwtService jwtService;
    @MockBean
    private ec.mil.dsndft.servicio_gestion.config.JwtAuthenticationFilter jwtAuthenticationFilter;





    @Test
    @DisplayName("Debe crear atención psicológica (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void crearAtencion_debeCrear() throws Exception {
        AtencionPsicologicaRequestDTO req = new AtencionPsicologicaRequestDTO();
        req.setPersonalMilitarId(1L);
        req.setPsicologoId(2L);
        req.setFechaAtencion(java.time.LocalDate.now());
        req.setEstado("ACTIVO");
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        Mockito.when(atencionService.crearAtencion(any())).thenReturn(resp);
        mockMvc.perform(post("/api/atenciones")
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
    @DisplayName("Debe obtener atención psicológica por ID (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void obtenerAtencion_debeRetornar() throws Exception {
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        Mockito.when(atencionService.obtenerPorId(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/atenciones/1"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe actualizar atención psicológica (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void actualizarAtencion_debeActualizar() throws Exception {
        AtencionPsicologicaRequestDTO req = new AtencionPsicologicaRequestDTO();
        req.setPersonalMilitarId(1L);
        req.setPsicologoId(2L);
        req.setFechaAtencion(java.time.LocalDate.now());
        req.setEstado("ACTIVO");
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        Mockito.when(atencionService.actualizarAtencion(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(put("/api/atenciones/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe eliminar atención psicológica (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void eliminarAtencion_debeEliminar() throws Exception {
        Mockito.doNothing().when(atencionService).eliminarAtencion(1L);
        mockMvc.perform(delete("/api/atenciones/1"))
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                if (status != 204 && status != 200) {
                    throw new AssertionError("Expected status 204 or 200 but got " + status);
                }
            });
    }

    @Test
    @DisplayName("Debe listar atenciones por psicólogo (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void listarPorPsicologo_debeRetornarLista() throws Exception {
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INICIAL");
        Mockito.when(atencionService.listarPorPsicologo(eq(1L), any())).thenReturn(new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1));
        String response = mockMvc.perform(get("/api/atenciones/psicologo/1?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        System.out.println("RESPUESTA listarPorPsicologo: " + response);
        mockMvc.perform(get("/api/atenciones/psicologo/1?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].tipoEvaluacion", is("INICIAL")));
    }

    @Test
    @DisplayName("Debe listar atenciones por paciente (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void listarPorPaciente_debeRetornarLista() throws Exception {
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INICIAL");
        Mockito.when(atencionService.listarPorPaciente(eq(1L), any())).thenReturn(new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1));
        String response = mockMvc.perform(get("/api/atenciones/paciente/1?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        System.out.println("RESPUESTA listarPorPaciente: " + response);
        mockMvc.perform(get("/api/atenciones/paciente/1?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].tipoEvaluacion", is("INICIAL")));
    }

    @Test
    @DisplayName("Debe listar atenciones por estado, nombre y fecha (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void listarPorFiltroAtencion_debeRetornarLista() throws Exception {
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPacienteNombreCompleto("LOPEZ PEREZ");
        resp.setFechaAtencion(java.time.LocalDate.of(2024,2,1));
        resp.setEstado("FINALIZADA");
        resp.setTipoEvaluacion("INICIAL");
        Mockito.when(atencionService.listarPorFiltroAtencion(eq("FINALIZADA"), eq("LOPEZ"), eq(java.time.LocalDate.of(2024,2,1)), any())).thenReturn(new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1));
        String response = mockMvc.perform(get("/api/atenciones/filtro-atencion?estadoAtencion=FINALIZADA&nombre=LOPEZ&fecha=2024-02-01&page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        System.out.println("RESPUESTA listarPorFiltroAtencion: " + response);
        mockMvc.perform(get("/api/atenciones/filtro-atencion?estadoAtencion=FINALIZADA&nombre=LOPEZ&fecha=2024-02-01&page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].pacienteNombreCompleto", is("LOPEZ PEREZ")))
            .andExpect(jsonPath("$.content[0].fechaAtencion", is("2024-02-01")))
            .andExpect(jsonPath("$.content[0].estado", is("FINALIZADA")))
            .andExpect(jsonPath("$.content[0].tipoEvaluacion", is("INICIAL")));
    }

    @Test
    @DisplayName("Debe finalizar atención psicológica (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void finalizarAtencion_debeFinalizar() throws Exception {
        AtencionPsicologicaRequestDTO req = new AtencionPsicologicaRequestDTO();
        req.setPersonalMilitarId(1L);
        req.setPsicologoId(2L);
        req.setFechaAtencion(java.time.LocalDate.now());
        req.setEstado("ACTIVO");
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        Mockito.when(atencionService.finalizarAtencion(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(post("/api/atenciones/1/finalizar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe cancelar atención psicológica (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void cancelarAtencion_debeCancelar() throws Exception {
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("CANCELADA");
        Mockito.when(atencionService.cancelarAtencion(eq(1L), anyString())).thenReturn(resp);
        mockMvc.perform(post("/api/atenciones/1/cancelar?razon=motivo"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe crear atención de seguimiento (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void crearAtencionSeguimiento_debeCrear() throws Exception {
        AtencionSeguimientoRequestDTO req = new AtencionSeguimientoRequestDTO();
        req.setFichaPsicologicaId(1L);
        req.setPsicologoId(2L);
        req.setFechaAtencion(java.time.LocalDate.now());
        AtencionPsicologicaResponseDTO resp = new AtencionPsicologicaResponseDTO();
        resp.setId(2L);
        resp.setFichaPsicologicaId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        Mockito.when(atencionService.crearAtencionSeguimiento(any())).thenReturn(resp);
        mockMvc.perform(post("/api/atenciones/seguimiento")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }
}
