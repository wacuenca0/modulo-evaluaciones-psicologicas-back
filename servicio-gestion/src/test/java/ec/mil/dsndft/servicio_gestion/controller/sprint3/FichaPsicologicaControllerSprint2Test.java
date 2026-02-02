package ec.mil.dsndft.servicio_gestion.controller.sprint3;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mil.dsndft.servicio_gestion.controller.FichaPsicologicaController;
import ec.mil.dsndft.servicio_gestion.model.dto.*;
import ec.mil.dsndft.servicio_gestion.service.FichaPsicologicaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias Sprint 2 - FichaPsicologicaController
 * Cobertura de los principales flujos de fichas psicológicas.
 */
@WebMvcTest(ec.mil.dsndft.servicio_gestion.controller.FichaPsicologicaController.class)
@Import(ec.mil.dsndft.servicio_gestion.config.TestSecurityConfig.class)
class FichaPsicologicaControllerSprint2Test {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FichaPsicologicaService fichaService;

    // Mock de seguridad para evitar errores de contexto
    @MockBean
    private ec.mil.dsndft.servicio_gestion.config.JwtService jwtService;
    @MockBean
    private ec.mil.dsndft.servicio_gestion.config.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Debe crear ficha psicológica (rol PSICOLOGO)")
    @WithMockUser(roles = {"PSICOLOGO"})
    void crearFicha_debeCrear() throws Exception {
        FichaDatosGeneralesRequestDTO req = new FichaDatosGeneralesRequestDTO();
        req.setPersonalMilitarId(1L);
        req.setPsicologoId(2L);
        req.setFechaEvaluacion(java.time.LocalDate.now());
        req.setTipoEvaluacion("INGRESO");
        req.setEstado("ACTIVO");
        FichaPsicologicaDTO resp = new FichaPsicologicaDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INGRESO");
        Mockito.when(fichaService.crearFicha(any())).thenReturn(resp);
        mockMvc.perform(post("/api/fichas-psicologicas")
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
    @DisplayName("Debe obtener ficha psicológica por número (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void obtenerFichaPorNumero_debeRetornar() throws Exception {
        FichaPsicologicaDTO resp = new FichaPsicologicaDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INGRESO");
        Mockito.when(fichaService.obtenerPorNumeroEvaluacion("F-001")).thenReturn(resp);
        mockMvc.perform(get("/api/fichas-psicologicas/numero/F-001"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe actualizar datos generales de ficha (rol PSICOLOGO)")
    @WithMockUser(roles = {"PSICOLOGO"})
    void actualizarDatosGenerales_debeActualizar() throws Exception {
        FichaDatosGeneralesRequestDTO req = new FichaDatosGeneralesRequestDTO();
        req.setPersonalMilitarId(1L);
        req.setPsicologoId(2L);
        req.setFechaEvaluacion(java.time.LocalDate.now());
        req.setTipoEvaluacion("INGRESO");
        req.setEstado("ACTIVO");
        FichaPsicologicaDTO resp = new FichaPsicologicaDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INGRESO");
        Mockito.when(fichaService.actualizarDatosGenerales(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(put("/api/fichas-psicologicas/1/general")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe listar fichas psicológicas (rol autorizado)")
    @WithMockUser(roles = {"ADMINISTRADOR", "PSICOLOGO"})
    void listarFichas_debeRetornarLista() throws Exception {
        FichaPsicologicaDTO resp = new FichaPsicologicaDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INGRESO");
        Mockito.when(fichaService.listar(any(), any(), any(), any(), any())).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/fichas-psicologicas"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe eliminar ficha psicológica (rol ADMINISTRADOR)")
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void eliminarFicha_debeEliminar() throws Exception {
        Mockito.doNothing().when(fichaService).eliminarFicha(1L);
        mockMvc.perform(delete("/api/fichas-psicologicas/1"))
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                if (status != 204 && status != 200) {
                    throw new AssertionError("Expected status 204 or 200 but got " + status);
                }
            });
    }

    @Test
    @DisplayName("Debe actualizar condición clínica (rol PSICOLOGO)")
    @WithMockUser(roles = {"PSICOLOGO"})
    void actualizarCondicion_debeActualizar() throws Exception {
        FichaCondicionRequestDTO req = new FichaCondicionRequestDTO();
        req.setCondicion("ESTABLE");
        FichaPsicologicaDTO resp = new FichaPsicologicaDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("ACTIVO");
        resp.setTipoEvaluacion("INGRESO");
        resp.setCondicion("ESTABLE");
        Mockito.when(fichaService.actualizarCondicion(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(put("/api/fichas-psicologicas/1/condicion")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe finalizar ficha psicológica (rol PSICOLOGO)")
    @WithMockUser(roles = {"PSICOLOGO"})
    void finalizarFicha_debeFinalizar() throws Exception {
        FichaPsicologicaDTO resp = new FichaPsicologicaDTO();
        resp.setId(1L);
        resp.setPersonalMilitarId(1L);
        resp.setPsicologoId(2L);
        resp.setEstado("FINALIZADA");
        resp.setTipoEvaluacion("INGRESO");
        Mockito.when(fichaService.finalizarFicha(1L)).thenReturn(resp);
        mockMvc.perform(post("/api/fichas-psicologicas/1/finalizar"))
            .andExpect(status().isOk());
    }
}
