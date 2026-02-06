// Archivo: model/dto/reportes/ReporteAtencionPsicologoDTO.java
package ec.mil.dsndft.servicio_gestion.model.dto.reportes;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteAtencionPsicologoDTO {
            public ReporteAtencionPsicologoDTO(Long psicologoId, String psicologoNombre, String psicologoUsername, String psicologoUnidadMilitar,
                    Long totalFichasAtendidas, Long fichasActivas, Long fichasObservacion, Long totalSeguimientos, Long personasAtendidas, LocalDate ultimaAtencion) {
                this.psicologoId = psicologoId;
                this.psicologoNombre = psicologoNombre;
                this.psicologoUsername = psicologoUsername;
                this.psicologoUnidadMilitar = psicologoUnidadMilitar;
                this.totalFichasAtendidas = totalFichasAtendidas;
                this.fichasActivas = fichasActivas;
                this.fichasObservacion = fichasObservacion;
                this.totalSeguimientos = totalSeguimientos;
                this.personasAtendidas = personasAtendidas;
                this.ultimaAtencion = ultimaAtencion;
            }
    private Long psicologoId;
    private String psicologoNombre;
    private String psicologoUsername;
    private String psicologoUnidadMilitar;
    private Long totalFichasAtendidas;
    private Long fichasActivas;
    private Long fichasObservacion;
    private Long totalSeguimientos;
    
    // NUEVO CAMPO PARA RF015: Cuenta de personas únicas atendidas
    private Long personasAtendidas;
    
    private LocalDate ultimaAtencion;

    // Estadísticas por acciones (basadas en historial)
    private Long totalAccionesProgramadas;
    private Long totalAccionesEnCurso;
    private Long totalAccionesFinalizadas;
    private Long totalAccionesCanceladas;
    private Long totalAccionesNoAsistio;
    private Long filtroDiagnosticoId;
    private String filtroDiagnosticoCodigo;
    private String filtroDiagnosticoTexto;
    private String filtroCedula;
    private String filtroUnidadMilitar;
    private LocalDate filtroFechaDesde;
    private LocalDate filtroFechaHasta;
}