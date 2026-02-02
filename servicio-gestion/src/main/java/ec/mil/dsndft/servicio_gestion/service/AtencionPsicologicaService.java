package ec.mil.dsndft.servicio_gestion.service;

import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AtencionPsicologicaService {
    
    AtencionPsicologicaResponseDTO crearAtencion(AtencionPsicologicaRequestDTO request);
    
    AtencionPsicologicaResponseDTO obtenerPorId(Long id);
    
    AtencionPsicologicaResponseDTO actualizarAtencion(Long id, AtencionPsicologicaRequestDTO request);
    
    void eliminarAtencion(Long id);
    
    Page<AtencionPsicologicaResponseDTO> listarPorPsicologo(Long psicologoId, Pageable pageable);

    Page<AtencionPsicologicaResponseDTO> listarPorPaciente(Long pacienteId, Pageable pageable);

    Page<AtencionPsicologicaResponseDTO> listarPorFiltroAtencion(String estadoAtencion, String nombre, java.time.LocalDate fecha, org.springframework.data.domain.Pageable pageable);
    
    // Métodos adicionales simples
    AtencionPsicologicaResponseDTO finalizarAtencion(Long id, AtencionPsicologicaRequestDTO request);
    
    AtencionPsicologicaResponseDTO cancelarAtencion(Long id, String razon);
    // Nuevo: crear atención de seguimiento para ficha en seguimiento
    AtencionPsicologicaResponseDTO crearAtencionSeguimiento(ec.mil.dsndft.servicio_gestion.model.dto.AtencionSeguimientoRequestDTO request);
}