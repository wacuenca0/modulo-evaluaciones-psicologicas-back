package ec.mil.dsndft.servicio_gestion.controller.sprint4;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mil.dsndft.servicio_gestion.controller.ReporteGestionController;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteAtencionPsicologoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteHistorialFichaDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReportePersonalDiagnosticoDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteSeguimientoTransferenciaDTO;
import ec.mil.dsndft.servicio_gestion.service.ReporteGestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteGestionController.class)
@Import(ec.mil.dsndft.servicio_gestion.config.TestSecurityConfig.class)
class ReporteGestionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReporteGestionService reporteGestionService;
    @MockBean
    private ec.mil.dsndft.servicio_gestion.config.JwtService jwtService;
    @MockBean
    private ec.mil.dsndft.servicio_gestion.config.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Debe obtener atenciones de psicólogos (rol OBSERVADOR)")
    @WithMockUser(roles = {"OBSERVADOR"})
    void obtenerAtenciones_debeRetornarOk() throws Exception {
        ReporteAtencionPsicologoDTO dto = new ReporteAtencionPsicologoDTO();
        Page<ReporteAtencionPsicologoDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        Mockito.when(reporteGestionService.obtenerAtencionesPorPsicologo(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mockMvc.perform(get("/api/reportes/atenciones-psicologos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe obtener personal diagnósticos (rol OBSERVADOR)")
    @WithMockUser(roles = {"OBSERVADOR"})
    void obtenerPersonalDiagnosticos_debeRetornarOk() throws Exception {
        ReportePersonalDiagnosticoDTO dto = new ReportePersonalDiagnosticoDTO();
        Page<ReportePersonalDiagnosticoDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        Mockito.when(reporteGestionService.obtenerReportePersonalDiagnostico(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mockMvc.perform(get("/api/reportes/personal-diagnosticos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe obtener seguimiento transferencia (rol OBSERVADOR)")
    @WithMockUser(roles = {"OBSERVADOR"})
    void obtenerSeguimientoTransferencia_debeRetornarOk() throws Exception {
        ReporteSeguimientoTransferenciaDTO dto = new ReporteSeguimientoTransferenciaDTO();
        Page<ReporteSeguimientoTransferenciaDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        Mockito.when(reporteGestionService.obtenerReporteSeguimientoTransferencia(any(), any(), any(), any(), any(), anyBoolean(), any())).thenReturn(page);
        mockMvc.perform(get("/api/reportes/seguimiento-transferencia"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe obtener historial fichas (rol OBSERVADOR)")
    @WithMockUser(roles = {"OBSERVADOR"})
    void obtenerHistorialFichas_debeRetornarOk() throws Exception {
        ReporteHistorialFichaDTO dto = new ReporteHistorialFichaDTO();
        Page<ReporteHistorialFichaDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        Mockito.when(reporteGestionService.obtenerHistorialFichas(any(), any(), anyBoolean(), any())).thenReturn(page);
        mockMvc.perform(get("/api/reportes/historial-fichas"))
                .andExpect(status().isOk());
    }
}
