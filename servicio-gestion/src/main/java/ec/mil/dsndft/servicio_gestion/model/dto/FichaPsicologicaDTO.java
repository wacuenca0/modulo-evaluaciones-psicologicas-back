
package ec.mil.dsndft.servicio_gestion.model.dto;

import ec.mil.dsndft.servicio_gestion.model.dto.ObservacionClinicaSectionDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.PsicoanamnesisPrenatalDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.PsicoanamnesisNatalDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.PsicoanamnesisInfanciaDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.DiagnosticoCie10DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FichaPsicologicaDTO {
    private Long id;
    private Long personalMilitarId;
    private String personalMilitarNombre;
    private String personalMilitarCedula;
    private Long psicologoId;
    private String psicologoNombre;
    private String psicologoUsername;
    private Long creadoPorId;
    private String creadoPorNombre;
    private String creadoPorUsername;
    private Long actualizadoPorId;
    private String actualizadoPorNombre;
    private String actualizadoPorUsername;
    private String numeroEvaluacion;
    private LocalDate fechaEvaluacion;
    private String tipoEvaluacion;
    private ObservacionClinicaSectionDTO seccionObservacion;
    private PsicoanamnesisPrenatalDTO seccionPrenatal;
    private PsicoanamnesisNatalDTO seccionNatal;
    private PsicoanamnesisInfanciaDTO seccionInfancia;
    private String estado;
    private String condicion;
    // Lista de diagnósticos CIE-10
    private java.util.List<DiagnosticoCie10DTO> diagnosticosCie10;
    private String planFrecuencia;
    private String planTipoSesion;
    private String planDetalle;
    private LocalDate ultimaFechaSeguimiento;
    private LocalDate proximoSeguimiento;
    private LocalDate transferenciaFecha;
    private String transferenciaUnidad;
    private String transferenciaObservacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}