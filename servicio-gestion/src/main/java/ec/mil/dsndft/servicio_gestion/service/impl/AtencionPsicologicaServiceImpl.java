
package ec.mil.dsndft.servicio_gestion.service.impl;

import ec.mil.dsndft.servicio_gestion.entity.FichaPsicologica;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionSeguimientoRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.enums.EstadoFichaEnum;
import ec.mil.dsndft.servicio_gestion.repository.FichaPsicologicaRepository;

import ec.mil.dsndft.servicio_gestion.entity.AtencionPsicologica;
import ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.entity.PersonalMilitar;
import ec.mil.dsndft.servicio_gestion.entity.Psicologo;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaResponseDTO;
import ec.mil.dsndft.servicio_gestion.repository.AtencionPsicologicaRepository;
import ec.mil.dsndft.servicio_gestion.repository.CatalogoDiagnosticoCie10Repository;
import ec.mil.dsndft.servicio_gestion.repository.PersonalMilitarRepository;
import ec.mil.dsndft.servicio_gestion.repository.PsicologoRepository;
import ec.mil.dsndft.servicio_gestion.service.AtencionPsicologicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
@Service
@RequiredArgsConstructor
public class AtencionPsicologicaServiceImpl implements AtencionPsicologicaService {

    private final AtencionPsicologicaRepository atencionRepository;
    private final PersonalMilitarRepository personalMilitarRepository;
    private final PsicologoRepository psicologoRepository;
    private final CatalogoDiagnosticoCie10Repository cie10Repository;
    private final FichaPsicologicaRepository fichaPsicologicaRepository;
    @Override
    @Transactional
    public AtencionPsicologicaResponseDTO crearAtencionSeguimiento(AtencionSeguimientoRequestDTO request) {
        // Buscar ficha y validar condicionClinica = SEGUIMIENTO
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(request.getFichaPsicologicaId())
            .orElseThrow(() -> new RuntimeException("Ficha psicológica no encontrada"));
        if (ficha.getCondicionClinica() == null ||
            !ficha.getCondicionClinica().name().equalsIgnoreCase("SEGUIMIENTO")) {
            throw new RuntimeException("Solo se pueden registrar atenciones para fichas en seguimiento");
        }

        // Buscar psicólogo
        Psicologo psicologo = psicologoRepository.findById(request.getPsicologoId())
            .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        // Diagnósticos
        List<CatalogoDiagnosticoCie10> diagnosticos = null;
        if (request.getDiagnosticoIds() != null && !request.getDiagnosticoIds().isEmpty()) {
            diagnosticos = cie10Repository.findAllById(request.getDiagnosticoIds());
        }

        // Crear la atención asociada a la ficha
        AtencionPsicologica atencion = AtencionPsicologica.builder()
            .personalMilitar(ficha.getPersonalMilitar())
            .psicologo(psicologo)
            .fichaPsicologica(ficha)
            .fechaAtencion(request.getFechaAtencion())
            .horaInicio(request.getHoraInicio())
            .horaFin(request.getHoraFin())
            .tipoAtencion("PRESENCIAL")
            .tipoConsulta("SEGUIMIENTO")
            .motivoConsulta(request.getMotivoConsulta())
            .planIntervencion(request.getPlanIntervencion())
            .recomendaciones(request.getRecomendaciones())
            .diagnosticos(diagnosticos)
            .proximaCita(request.getProximaCita())
            .observacionesProximaCita(request.getObservacionesProximaCita())
            .estado("PROGRAMADA")
            .activo(true)
            .build();

        AtencionPsicologica saved = atencionRepository.save(atencion);
        return convertirAResponseDTO(saved);
    }

    @Override
    @Transactional
    public AtencionPsicologicaResponseDTO crearAtencion(AtencionPsicologicaRequestDTO request) {
        // Buscar paciente
        PersonalMilitar paciente = personalMilitarRepository.findById(request.getPersonalMilitarId())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Buscar psicólogo
        Psicologo psicologo = psicologoRepository.findById(request.getPsicologoId())
            .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));
        
        // Obtener diagnósticos si se proporcionaron
        List<CatalogoDiagnosticoCie10> diagnosticos = null;
        if (request.getDiagnosticoIds() != null && !request.getDiagnosticoIds().isEmpty()) {
            diagnosticos = cie10Repository.findAllById(request.getDiagnosticoIds());
        }
        
        // Calcular número de sesión
        Integer numeroSesion = calcularNumeroSesion(paciente.getId(), request.getTipoConsulta());
        
        // Crear la entidad
        AtencionPsicologica atencion = AtencionPsicologica.builder()
                .personalMilitar(paciente)
                .psicologo(psicologo)
                .fechaAtencion(request.getFechaAtencion())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .numeroSesion(numeroSesion)
                .tipoAtencion(request.getTipoAtencion() != null ? request.getTipoAtencion() : "PRESENCIAL")
                .tipoConsulta(request.getTipoConsulta() != null ? request.getTipoConsulta() : "PRIMERA_VEZ")
                .motivoConsulta(request.getMotivoConsulta())
                .anamnesis(request.getAnamnesis())
                .examenMental(request.getExamenMental())
                .impresionDiagnostica(request.getImpresionDiagnostica())
                .planIntervencion(request.getPlanIntervencion())
                .recomendaciones(request.getRecomendaciones())
                .derivacion(request.getDerivacion())
                .diagnosticos(diagnosticos)
                .proximaCita(request.getProximaCita())
                .observacionesProximaCita(request.getObservacionesProximaCita())
                .estado(request.getEstado() != null ? request.getEstado() : "PROGRAMADA")
                .razonCancelacion(request.getRazonCancelacion())
                .activo(true)
                .build();
        
        AtencionPsicologica saved = atencionRepository.save(atencion);
        return convertirAResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AtencionPsicologicaResponseDTO obtenerPorId(Long id) {
        AtencionPsicologica atencion = atencionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
        
        return convertirAResponseDTO(atencion);
    }

    @Override
    @Transactional
    public AtencionPsicologicaResponseDTO actualizarAtencion(Long id, AtencionPsicologicaRequestDTO request) {
        AtencionPsicologica atencion = atencionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
        
        // Actualizar campos
        if (request.getHoraInicio() != null) atencion.setHoraInicio(request.getHoraInicio());
        if (request.getHoraFin() != null) atencion.setHoraFin(request.getHoraFin());
        if (request.getTipoAtencion() != null) atencion.setTipoAtencion(request.getTipoAtencion());
        if (request.getMotivoConsulta() != null) atencion.setMotivoConsulta(request.getMotivoConsulta());
        if (request.getAnamnesis() != null) atencion.setAnamnesis(request.getAnamnesis());
        if (request.getExamenMental() != null) atencion.setExamenMental(request.getExamenMental());
        if (request.getImpresionDiagnostica() != null) atencion.setImpresionDiagnostica(request.getImpresionDiagnostica());
        if (request.getPlanIntervencion() != null) atencion.setPlanIntervencion(request.getPlanIntervencion());
        if (request.getRecomendaciones() != null) atencion.setRecomendaciones(request.getRecomendaciones());
        if (request.getDerivacion() != null) atencion.setDerivacion(request.getDerivacion());
        if (request.getProximaCita() != null) atencion.setProximaCita(request.getProximaCita());
        if (request.getObservacionesProximaCita() != null) atencion.setObservacionesProximaCita(request.getObservacionesProximaCita());
        if (request.getEstado() != null) atencion.setEstado(request.getEstado());
        if (request.getRazonCancelacion() != null) atencion.setRazonCancelacion(request.getRazonCancelacion());
        
        // Actualizar diagnósticos
        if (request.getDiagnosticoIds() != null) {
            List<CatalogoDiagnosticoCie10> diagnosticos = cie10Repository.findAllById(request.getDiagnosticoIds());
            atencion.setDiagnosticos(diagnosticos);
        }
        
        AtencionPsicologica updated = atencionRepository.save(atencion);
        return convertirAResponseDTO(updated);
    }

    @Override
    @Transactional
    public void eliminarAtencion(Long id) {
        AtencionPsicologica atencion = atencionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
        
        atencion.setActivo(false);
        atencionRepository.save(atencion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AtencionPsicologicaResponseDTO> listarPorPsicologo(Long psicologoId, Pageable pageable) {
        Page<AtencionPsicologica> atencionesPage = atencionRepository.findByPsicologoId(psicologoId, pageable);
        List<AtencionPsicologicaResponseDTO> list = atencionesPage.getContent().stream()
            .filter(AtencionPsicologica::getActivo)
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, atencionesPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AtencionPsicologicaResponseDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        Page<AtencionPsicologica> atencionesPage = atencionRepository.findByPersonalMilitarId(pacienteId, pageable);
        List<AtencionPsicologicaResponseDTO> list = atencionesPage.getContent().stream()
            .filter(AtencionPsicologica::getActivo)
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, atencionesPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AtencionPsicologicaResponseDTO> listarPorFiltroAtencion(String estadoAtencion, String nombre, LocalDate fecha, Pageable pageable) {
        Page<AtencionPsicologica> atencionesPage = atencionRepository.findByFiltroAtencion(estadoAtencion, nombre, fecha, pageable);
        List<AtencionPsicologicaResponseDTO> list = atencionesPage.getContent().stream()
            .filter(AtencionPsicologica::getActivo)
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, atencionesPage.getTotalElements());
    }

    private <T> Page<T> paginarEnMemoria(List<T> list, Pageable pageable) {
        int total = list.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<T> sublist = (start > end) ? List.of() : list.subList(start, end);
        return new PageImpl<>(sublist, pageable, total);
    }

    @Override
    @Transactional
    public AtencionPsicologicaResponseDTO finalizarAtencion(Long id, AtencionPsicologicaRequestDTO request) {
        AtencionPsicologica atencion = atencionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
        
        // Actualizar campos clínicos
        if (request.getAnamnesis() != null) atencion.setAnamnesis(request.getAnamnesis());
        if (request.getExamenMental() != null) atencion.setExamenMental(request.getExamenMental());
        if (request.getImpresionDiagnostica() != null) atencion.setImpresionDiagnostica(request.getImpresionDiagnostica());
        if (request.getPlanIntervencion() != null) atencion.setPlanIntervencion(request.getPlanIntervencion());
        if (request.getRecomendaciones() != null) atencion.setRecomendaciones(request.getRecomendaciones());
        if (request.getDerivacion() != null) atencion.setDerivacion(request.getDerivacion());
        
        // Actualizar diagnósticos
        if (request.getDiagnosticoIds() != null && !request.getDiagnosticoIds().isEmpty()) {
            List<CatalogoDiagnosticoCie10> diagnosticos = cie10Repository.findAllById(request.getDiagnosticoIds());
            atencion.setDiagnosticos(diagnosticos);
        }
        
        // Cambiar estado a FINALIZADA
        atencion.setEstado("FINALIZADA");
        
        AtencionPsicologica updated = atencionRepository.save(atencion);
        return convertirAResponseDTO(updated);
    }

    @Override
    @Transactional
    public AtencionPsicologicaResponseDTO cancelarAtencion(Long id, String razon) {
        AtencionPsicologica atencion = atencionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
        
        atencion.setEstado("CANCELADA");
        atencion.setRazonCancelacion(razon);
        
        AtencionPsicologica updated = atencionRepository.save(atencion);
        return convertirAResponseDTO(updated);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private Integer calcularNumeroSesion(Long pacienteId, String tipoConsulta) {
        if ("PRIMERA_VEZ".equals(tipoConsulta)) {
            return 1;
        }
        
        long atencionesPrevias = atencionRepository.findAll().stream()
                .filter(a -> a.getActivo())
                .filter(a -> a.getPersonalMilitar().getId().equals(pacienteId))
                .filter(a -> "FINALIZADA".equals(a.getEstado()))
                .count();
        
        return (int) atencionesPrevias + 1;
    }
    
    private AtencionPsicologicaResponseDTO convertirAResponseDTO(AtencionPsicologica atencion) {
        AtencionPsicologicaResponseDTO dto = new AtencionPsicologicaResponseDTO();
        dto.setId(atencion.getId());
        dto.setFichaPsicologicaId(atencion.getFichaPsicologica() != null ? atencion.getFichaPsicologica().getId() : null);
        dto.setPersonalMilitarId(atencion.getPersonalMilitar().getId());
        dto.setPacienteCedula(atencion.getPersonalMilitar().getCedula());
        dto.setPacienteNombreCompleto(atencion.getPersonalMilitar().getApellidosNombres());
        dto.setPacienteGrado(atencion.getPersonalMilitar().getGrado());
        dto.setPacienteUnidadMilitar(atencion.getPersonalMilitar().getUnidadMilitar());
        dto.setPsicologoId(atencion.getPsicologo().getId());
        dto.setPsicologoCedula(atencion.getPsicologo().getCedula());
        dto.setPsicologoNombreCompleto(atencion.getPsicologo().getApellidosNombres());
        dto.setFechaAtencion(atencion.getFechaAtencion());
        dto.setHoraInicio(atencion.getHoraInicio());
        dto.setHoraFin(atencion.getHoraFin());
        dto.setNumeroSesion(atencion.getNumeroSesion());
        dto.setTipoAtencion(atencion.getTipoAtencion());
        dto.setTipoConsulta(atencion.getTipoConsulta());
        dto.setMotivoConsulta(atencion.getMotivoConsulta());
        dto.setAnamnesis(atencion.getAnamnesis());
        dto.setExamenMental(atencion.getExamenMental());
        dto.setImpresionDiagnostica(atencion.getImpresionDiagnostica());
        dto.setPlanIntervencion(atencion.getPlanIntervencion());
        dto.setRecomendaciones(atencion.getRecomendaciones());
        dto.setDerivacion(atencion.getDerivacion());
        dto.setDiagnosticos(convertirDiagnosticosADTO(atencion.getDiagnosticos()));
        dto.setProximaCita(atencion.getProximaCita());
        dto.setObservacionesProximaCita(atencion.getObservacionesProximaCita());
        dto.setEstado(atencion.getEstado());
        dto.setRazonCancelacion(atencion.getRazonCancelacion());
        dto.setActivo(atencion.getActivo());
        dto.setCreatedAt(atencion.getCreatedAt());
        dto.setUpdatedAt(atencion.getUpdatedAt());
        dto.setDuracionMinutos(atencion.obtenerDuracionMinutos());
        // Asignar tipoEvaluacion si existe fichaPsicologica
        if (atencion.getFichaPsicologica() != null) {
            dto.setTipoEvaluacion(atencion.getFichaPsicologica().getTipoEvaluacion());
        } else {
            dto.setTipoEvaluacion(null);
        }
        return dto;
    }
    
    private List<AtencionPsicologicaResponseDTO.DiagnosticoCie10DTO> convertirDiagnosticosADTO(
            List<CatalogoDiagnosticoCie10> diagnosticos) {
        if (diagnosticos == null) {
            return List.of();
        }
        
        return diagnosticos.stream()
            .map(d -> {
                AtencionPsicologicaResponseDTO.DiagnosticoCie10DTO dto = new AtencionPsicologicaResponseDTO.DiagnosticoCie10DTO();
                dto.setId(d.getId());
                dto.setCodigo(d.getCodigo());
                dto.setNombre(d.getNombre());
                dto.setDescripcion(d.getDescripcion());
                return dto;
            })
            .collect(Collectors.toList());
    }
}