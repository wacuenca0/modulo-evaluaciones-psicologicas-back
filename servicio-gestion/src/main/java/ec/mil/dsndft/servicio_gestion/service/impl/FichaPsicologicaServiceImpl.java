package ec.mil.dsndft.servicio_gestion.service.impl;

import ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.entity.FichaPsicologica;
import ec.mil.dsndft.servicio_gestion.entity.PersonalMilitar;
import ec.mil.dsndft.servicio_gestion.entity.Psicologo;
import ec.mil.dsndft.servicio_gestion.model.dto.*;
import ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.EstadoFichaEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.FrecuenciaSeguimientoEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.GradoDiscapacidadEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.GradoSociabilidadEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.RelacionFamiliarEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.TipoEvaluacionEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.TipoSesionEnum;
import ec.mil.dsndft.servicio_gestion.model.mapper.FichaPsicologicaMapper;
import ec.mil.dsndft.servicio_gestion.model.value.*;
import ec.mil.dsndft.servicio_gestion.repository.CatalogoDiagnosticoCie10Repository;
import ec.mil.dsndft.servicio_gestion.repository.FichaPsicologicaRepository;
import ec.mil.dsndft.servicio_gestion.repository.PersonalMilitarRepository;
import ec.mil.dsndft.servicio_gestion.service.FichaPsicologicaService;
import ec.mil.dsndft.servicio_gestion.service.support.AuthenticatedPsicologoProvider;
import ec.mil.dsndft.servicio_gestion.service.support.FichaCondicionManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FichaPsicologicaServiceImpl implements FichaPsicologicaService {
    
    private final FichaPsicologicaRepository fichaPsicologicaRepository;
    private final PersonalMilitarRepository personalMilitarRepository;
    private final FichaPsicologicaMapper mapper;
    private final FichaCondicionManager fichaCondicionManager;
    private final AuthenticatedPsicologoProvider psicologoAutenticadoProvider;
    private final CatalogoDiagnosticoCie10Repository catalogoDiagnosticoRepository;
    

    // ============================================
    // MÉTODOS EXISTENTES (sin cambios)
    // ============================================

    @Override
    @Transactional
    public void eliminarFicha(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        fichaPsicologicaRepository.delete(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichaPsicologicaDTO> listar(Long psicologoId, Long personalMilitarId, String estado, String condicion, Boolean soloActivas) {
        Long psicologoFiltro = ajustarFiltroPsicologo(psicologoId);
        EstadoFichaEnum estadoFiltro = resolveEstadoOptional(estado);
        CondicionClinicaEnum condicionFiltro = resolveCondicionOptional(condicion);
        return mapper.toDTOs(
            fichaPsicologicaRepository.findByFilters(psicologoFiltro, personalMilitarId, estadoFiltro, condicionFiltro, soloActivas)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichaPsicologicaDTO> listarPorCondicion(String condicion, Long psicologoId, Long personalMilitarId) {
        CondicionClinicaEnum condicionFiltro = resolveCondicionRequired(condicion);
        Long psicologoFiltro = ajustarFiltroPsicologo(psicologoId);
        return mapper.toDTOs(
            fichaPsicologicaRepository.findByFilters(psicologoFiltro, personalMilitarId, null, condicionFiltro, null)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FichaPsicologicaDTO obtenerPorId(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public FichaPsicologicaDTO obtenerPorNumeroEvaluacion(String numeroEvaluacion) {
        if (numeroEvaluacion == null || numeroEvaluacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de evaluación es obligatorio");
        }
        FichaPsicologica ficha = fichaPsicologicaRepository.findByNumeroEvaluacion(numeroEvaluacion.trim())
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada para el número indicado"));
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO crearFicha(FichaDatosGeneralesRequestDTO request) {
        Psicologo psicologo = obtenerPsicologoAutenticado(null);
        PersonalMilitar personal = personalMilitarRepository.findById(request.getPersonalMilitarId())
            .orElseThrow(() -> new EntityNotFoundException("Personal militar no encontrado"));

        FichaPsicologica ficha = new FichaPsicologica();
        ficha.setPsicologo(psicologo);
        ficha.setCreadoPor(psicologo);
        ficha.setActualizadoPor(psicologo);
        ficha.setPersonalMilitar(personal);
        ficha.setNumeroEvaluacion(generarNumeroEvaluacionUnico());
        aplicarDatosGenerales(ficha, request);
        // Asignar valor por defecto a condicionClinica para cumplir NOT NULL en Oracle
        ficha.setCondicionClinica(ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum.ALTA);
        ficha.setDiagnosticosCie10(null);
        ficha.setPlanSeguimiento(null);
        ficha.setCreatedAt(LocalDateTime.now());
        ficha.setUpdatedAt(LocalDateTime.now());

        FichaPsicologica guardada = fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarDatosGenerales(Long id, FichaDatosGeneralesRequestDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        PersonalMilitar personal = personalMilitarRepository.findById(request.getPersonalMilitarId())
            .orElseThrow(() -> new EntityNotFoundException("Personal militar no encontrado"));

        ficha.setPsicologo(psicologo);
        ficha.setActualizadoPor(psicologo);
        ficha.setPersonalMilitar(personal);
        aplicarDatosGenerales(ficha, request);
        ficha.setUpdatedAt(LocalDateTime.now());

        FichaPsicologica guardada = fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionObservacion(Long id, FichaSeccionObservacionRequestDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionObservacion(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionPsicoanamnesis(Long id, FichaSeccionPsicoanamnesisRequestDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionPsicoanamnesis(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarCondicion(Long id, FichaCondicionRequestDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        CondicionClinicaEnum condicion = resolveCondicionRequired(request.getCondicion());

        // Si viene observación desde el modal, la persistimos en la ficha
        // (sin exigir motivoConsulta aquí, porque ese flujo se gestiona en su sección)
        aplicarObservacionCondicion(ficha, request.getObservaciones());

        // No permitir cambios de condición en fichas ya cerradas o archivadas
        if (ficha.getEstado() == EstadoFichaEnum.CERRADA || ficha.getEstado() == EstadoFichaEnum.ARCHIVADA) {
            throw new IllegalArgumentException("La ficha ya se encuentra cerrada o archivada y no permite cambios en la condición clínica");
        }

        // Validaciones de negocio específicas por condición
        if ((CondicionClinicaEnum.SEGUIMIENTO.equals(condicion) || CondicionClinicaEnum.TRANSFERENCIA.equals(condicion))
            && (request.getDiagnosticosCie10() == null || request.getDiagnosticosCie10().isEmpty())) {
            throw new IllegalArgumentException("Debe seleccionar al menos un diagnóstico CIE-10 para la condición seleccionada");
        }

        ficha.setCondicionClinica(condicion);

        // Asignar diagnósticos CIE-10 (múltiples)
        if (request.getDiagnosticosCie10() != null && !request.getDiagnosticosCie10().isEmpty()) {
            List<CatalogoDiagnosticoCie10> diagnosticos = request.getDiagnosticosCie10().stream()
                .map(dto -> catalogoDiagnosticoRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Diagnóstico CIE-10 no encontrado: " + dto.getId())))
                .collect(java.util.stream.Collectors.toList());
            ficha.setDiagnosticosCie10(diagnosticos);
        } else {
            ficha.setDiagnosticosCie10(java.util.Collections.emptyList());
        }

        // Construir/limpiar plan de seguimiento según la condición
        if (CondicionClinicaEnum.SEGUIMIENTO.equals(condicion) || CondicionClinicaEnum.TRANSFERENCIA.equals(condicion)) {
            PlanSeguimiento plan = buildPlan(ficha, condicion, request.getPlanFrecuencia(), request.getPlanTipoSesion(), request.getPlanDetalle(), true);
            ficha.setPlanSeguimiento(plan);
        } else {
            // Para ALTA u otras condiciones, no mantener plan de seguimiento
            ficha.setPlanSeguimiento(null);
        }

        // Aplicar metadatos de seguimiento / transferencia (proximoSeguimiento, transferenciaInfo, etc.)
        aplicarMetadatosCondicion(ficha, condicion, request.getProximoSeguimiento(), request.getTransferenciaUnidad(), request.getTransferenciaObservacion());

        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(java.time.LocalDateTime.now());
        FichaPsicologica guardada = fichaPsicologicaRepository.save(ficha);
        // Eliminado: actualizarProgramacionSeguimiento para seguimientos
        return mapper.toDTO(guardada);
    }

    private void aplicarObservacionCondicion(FichaPsicologica ficha, String observacionesRaw) {
        String observacion = trimOrNull(observacionesRaw);
        if (observacion == null) {
            return;
        }
        if (ficha.getSeccionObservacion() == null) {
            ficha.setSeccionObservacion(new ec.mil.dsndft.servicio_gestion.model.value.ObservacionClinica());
        }
        ficha.getSeccionObservacion().setObservacionClinica(observacion);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarEstado(Long id, String estado) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        EstadoFichaEnum nuevoEstado = resolveEstadoRequired(estado);
        if (EstadoFichaEnum.CERRADA.equals(nuevoEstado)) {
            validarFichaListaParaCierre(ficha);
        }
        ficha.setEstado(nuevoEstado);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public String generarNumeroEvaluacionPreview() {
        return generarNumeroEvaluacionUnico();
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionObservacion(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        ficha.setSeccionObservacion(null);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionPsicoanamnesis(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        ficha.setSeccionPrenatal(null);
        ficha.setSeccionNatal(null);
        ficha.setSeccionInfancia(null);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO limpiarCondicionClinica(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        ficha.setCondicionClinica(CondicionClinicaEnum.ALTA);
        ficha.setDiagnosticosCie10(java.util.Collections.emptyList());
        ficha.setPlanSeguimiento(null);
        ficha.setTransferenciaInfo(null);
        ficha.setUltimaFechaSeguimiento(null);
        ficha.setProximoSeguimiento(null);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO finalizarFicha(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));

        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        if (!EstadoFichaEnum.CERRADA.equals(ficha.getEstado())) {
            validarFichaListaParaCierre(ficha);
            ficha.setEstado(EstadoFichaEnum.CERRADA);
            ficha.setActualizadoPor(psicologo);
            ficha.setUpdatedAt(LocalDateTime.now());
            fichaPsicologicaRepository.save(ficha);
        }
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichaPsicologicaDTO> obtenerHistorialPorPersonal(Long personalMilitarId) {
        if (personalMilitarId == null) {
            throw new IllegalArgumentException("El identificador del personal militar es obligatorio");
        }
        boolean existe = personalMilitarRepository.existsById(personalMilitarId);
        if (!existe) {
            throw new EntityNotFoundException("Personal militar no encontrado");
        }
        List<FichaPsicologica> fichas = fichaPsicologicaRepository
            .findByPersonalMilitarIdOrderByFechaEvaluacionDesc(personalMilitarId);
        return mapper.toDTOs(fichas);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<FichaPsicologicaDTO> obtenerHistorialPorPersonalPaginado(
            Long personalMilitarId,
            String cedulaPsicologo,
            java.time.LocalDate fechaDesde,
            java.time.LocalDate fechaHasta,
            org.springframework.data.domain.Pageable pageable) {
        if (personalMilitarId == null) {
            throw new IllegalArgumentException("El identificador del personal militar es obligatorio");
        }
        boolean existe = personalMilitarRepository.existsById(personalMilitarId);
        if (!existe) {
            throw new EntityNotFoundException("Personal militar no encontrado");
        }
        org.springframework.data.domain.Page<FichaPsicologica> pageEntities = fichaPsicologicaRepository
                .findHistorialByFilters(personalMilitarId,
                        (cedulaPsicologo == null || cedulaPsicologo.isBlank()) ? null : cedulaPsicologo.trim(),
                        fechaDesde,
                        fechaHasta,
                        pageable);
        java.util.List<FichaPsicologicaDTO> content = mapper.toDTOs(pageEntities.getContent());
        return new org.springframework.data.domain.PageImpl<>(content, pageable, pageEntities.getTotalElements());
    }

    // ============================================
    // NUEVOS MÉTODOS PARA LAS SECCIONES ADICIONALES
    // ============================================

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionAdolescencia(Long id, AdolescenciaJuventudAdultezDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionAdolescencia(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarSeccionAdolescencia(Long id, AdolescenciaJuventudAdultezDTO request) {
        return guardarSeccionAdolescencia(id, request);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionAdolescencia(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        fichaPsicologicaRepository.limpiarSeccionAdolescencia(id);
        
        if (ficha.getSeccionAdolescencia() != null) {
            ficha.getSeccionAdolescencia().setHabilidadesSociales(null);
            ficha.getSeccionAdolescencia().setTrastorno(null);
            ficha.getSeccionAdolescencia().setHistoriaPersonal(null);
            ficha.getSeccionAdolescencia().setMaltratoAdultoProblemasNegligencia(null);
            ficha.getSeccionAdolescencia().setProblemasRelacionadosCircunstanciasLegales(null);
            ficha.getSeccionAdolescencia().setTratamientosPsicologicosPsiquiatricos(null);
            ficha.getSeccionAdolescencia().setObservacion(null);
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionFamiliar(Long id, PsicoanamnesisFamiliarDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionFamiliar(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarSeccionFamiliar(Long id, PsicoanamnesisFamiliarDTO request) {
        return guardarSeccionFamiliar(id, request);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionFamiliar(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        fichaPsicologicaRepository.limpiarSeccionFamiliar(id);
        
        if (ficha.getSeccionFamiliar() != null) {
            ficha.getSeccionFamiliar().setMiembrosConQuienesConvive(null);
            ficha.getSeccionFamiliar().setAntecedentesPatologicosFamiliares(null);
            ficha.getSeccionFamiliar().setTieneAlgunaEnfermedad(null);
            ficha.getSeccionFamiliar().setTipoEnfermedad(null);
            ficha.getSeccionFamiliar().setObservacion(null);
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionFuncionesPsicologicas(Long id, ExamenFuncionesPsicologicasDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionFuncionesPsicologicas(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarSeccionFuncionesPsicologicas(Long id, ExamenFuncionesPsicologicasDTO request) {
        return guardarSeccionFuncionesPsicologicas(id, request);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionFuncionesPsicologicas(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        fichaPsicologicaRepository.limpiarSeccionFuncionesPsicologicas(id);
        
        if (ficha.getSeccionFuncionesPsicologicas() != null) {
            ficha.getSeccionFuncionesPsicologicas().setOrientacion(null);
            ficha.getSeccionFuncionesPsicologicas().setAtencion(null);
            ficha.getSeccionFuncionesPsicologicas().setSensopercepciones(null);
            ficha.getSeccionFuncionesPsicologicas().setVoluntad(null);
            ficha.getSeccionFuncionesPsicologicas().setJuicioRazonamiento(null);
            ficha.getSeccionFuncionesPsicologicas().setNutricion(null);
            ficha.getSeccionFuncionesPsicologicas().setSueno(null);
            ficha.getSeccionFuncionesPsicologicas().setSexual(null);
            ficha.getSeccionFuncionesPsicologicas().setPensamientoCurso(null);
            ficha.getSeccionFuncionesPsicologicas().setPensamientoEstructura(null);
            ficha.getSeccionFuncionesPsicologicas().setPensamientoContenido(null);
            ficha.getSeccionFuncionesPsicologicas().setConcienciaEnfermedadTratamiento(null);
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionRasgosExamenes(Long id, RasgosPersonalidadExamenesDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionRasgosExamenes(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarSeccionRasgosExamenes(Long id, RasgosPersonalidadExamenesDTO request) {
        return guardarSeccionRasgosExamenes(id, request);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionRasgosExamenes(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        fichaPsicologicaRepository.limpiarSeccionRasgosExamenes(id);
        
        if (ficha.getSeccionRasgosExamenes() != null) {
            ficha.getSeccionRasgosExamenes().setRasgo(null);
            ficha.getSeccionRasgosExamenes().setObservacion(null);
            ficha.getSeccionRasgosExamenes().setExamenesPsicologicos(null);
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO guardarSeccionEtiopatogenicaPronostico(Long id, FormulacionEtiopatogenicaPronosticoDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        aplicarSeccionEtiopatogenicaPronostico(ficha, request);
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarSeccionEtiopatogenicaPronostico(Long id, FormulacionEtiopatogenicaPronosticoDTO request) {
        return guardarSeccionEtiopatogenicaPronostico(id, request);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO eliminarSeccionEtiopatogenicaPronostico(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        fichaPsicologicaRepository.limpiarSeccionEtiopatogenicaPronostico(id);
        
        if (ficha.getSeccionEtiopatogenicaPronostico() != null) {
            ficha.getSeccionEtiopatogenicaPronostico().setFactoresPredisponentes(null);
            ficha.getSeccionEtiopatogenicaPronostico().setFactoresDeterminantes(null);
            ficha.getSeccionEtiopatogenicaPronostico().setFactoresDesencadenantes(null);
            ficha.getSeccionEtiopatogenicaPronostico().setPronosticoTipo(null);
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional
    public FichaPsicologicaDTO actualizarTodasSeccionesNuevas(Long id, FichaSeccionesNuevasRequestDTO request) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        if (request.getSeccionAdolescencia() != null) {
            aplicarSeccionAdolescencia(ficha, request.getSeccionAdolescencia());
        }
        
        if (request.getSeccionFamiliar() != null) {
            aplicarSeccionFamiliar(ficha, request.getSeccionFamiliar());
        }
        
        if (request.getSeccionFuncionesPsicologicas() != null) {
            aplicarSeccionFuncionesPsicologicas(ficha, request.getSeccionFuncionesPsicologicas());
        }
        
        if (request.getSeccionRasgosExamenes() != null) {
            aplicarSeccionRasgosExamenes(ficha, request.getSeccionRasgosExamenes());
        }
        
        if (request.getSeccionEtiopatogenicaPronostico() != null) {
            aplicarSeccionEtiopatogenicaPronostico(ficha, request.getSeccionEtiopatogenicaPronostico());
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        fichaPsicologicaRepository.save(ficha);
        return mapper.toDTO(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public FichaPsicologicaCompletaDTO obtenerFichaCompletaPorId(Long id) {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ficha psicológica no encontrada"));
        return mapper.toCompletaDTO(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarSeccionesNuevasCompletas(Long id) {
        Optional<Boolean> resultado = fichaPsicologicaRepository.tieneCompletasNuevasSecciones(id);
        return resultado.orElse(false);
    }

    @Override
    @Transactional
    public FichaPsicologicaCompletaDTO crearFichaCompleta(FichaPsicologicaCompletaRequestDTO request) {
        // Primero crear la ficha básica
        FichaPsicologicaDTO fichaBasica = crearFicha(request.getDatosGenerales());
        
        // Obtener la ficha creada
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(fichaBasica.getId())
            .orElseThrow(() -> new EntityNotFoundException("Ficha no encontrada después de creación"));
        
        Psicologo psicologo = obtenerPsicologoAutenticado(ficha.getPsicologo());
        
        // Aplicar las secciones adicionales si existen
        if (request.getSeccionObservacion() != null) {
            aplicarSeccionObservacion(ficha, request.getSeccionObservacion());
        }
        
        if (request.getSeccionPsicoanamnesis() != null) {
            aplicarSeccionPsicoanamnesis(ficha, request.getSeccionPsicoanamnesis());
        }
        
        if (request.getSeccionAdolescencia() != null) {
            aplicarSeccionAdolescencia(ficha, request.getSeccionAdolescencia());
        }
        
        if (request.getSeccionFamiliar() != null) {
            aplicarSeccionFamiliar(ficha, request.getSeccionFamiliar());
        }
        
        if (request.getSeccionFuncionesPsicologicas() != null) {
            aplicarSeccionFuncionesPsicologicas(ficha, request.getSeccionFuncionesPsicologicas());
        }
        
        if (request.getSeccionRasgosExamenes() != null) {
            aplicarSeccionRasgosExamenes(ficha, request.getSeccionRasgosExamenes());
        }
        
        if (request.getSeccionEtiopatogenicaPronostico() != null) {
            aplicarSeccionEtiopatogenicaPronostico(ficha, request.getSeccionEtiopatogenicaPronostico());
        }
        
        if (request.getSeccionDiagnosticoCondicion() != null) {
            // Aquí necesitarías implementar la lógica para aplicar la condición
            // Esto podría requerir llamar a actualizarCondicion() o implementar la lógica directamente
            log.info("Sección diagnóstico/condición ignorada en creación completa. Debe aplicarse separadamente.");
        }
        
        ficha.setActualizadoPor(psicologo);
        ficha.setUpdatedAt(LocalDateTime.now());
        FichaPsicologica guardada = fichaPsicologicaRepository.save(ficha);
        
        return mapper.toCompletaDTO(guardada);
    }

    // ============================================
    // MÉTODOS PRIVADOS PARA APLICAR LAS SECCIONES
    // ============================================

    private void aplicarSeccionAdolescencia(FichaPsicologica ficha, AdolescenciaJuventudAdultezDTO request) {
        if (ficha.getSeccionAdolescencia() == null) {
            ficha.setSeccionAdolescencia(new PsicoanamnesisAdolescenciaJuventudAdultez());
        }
        ficha.getSeccionAdolescencia().setHabilidadesSociales(trimOrNull(request.getHabilidadesSociales()));
        ficha.getSeccionAdolescencia().setTrastorno(trimOrNull(request.getTrastorno()));
        ficha.getSeccionAdolescencia().setHistoriaPersonal(trimOrNull(request.getHistoriaPersonal()));
        ficha.getSeccionAdolescencia().setMaltratoAdultoProblemasNegligencia(trimOrNull(request.getMaltratoAdultoProblemasNegligencia()));
        ficha.getSeccionAdolescencia().setProblemasRelacionadosCircunstanciasLegales(trimOrNull(request.getProblemasRelacionadosCircunstanciasLegales()));
        ficha.getSeccionAdolescencia().setTratamientosPsicologicosPsiquiatricos(trimOrNull(request.getTratamientosPsicologicosPsiquiatricos()));
        ficha.getSeccionAdolescencia().setObservacion(trimOrNull(request.getObservacion()));
    }

    private void aplicarSeccionFamiliar(FichaPsicologica ficha, PsicoanamnesisFamiliarDTO request) {
        if (ficha.getSeccionFamiliar() == null) {
            ficha.setSeccionFamiliar(new PsicoanamnesisFamiliar());
        }
        ficha.getSeccionFamiliar().setMiembrosConQuienesConvive(trimOrNull(request.getMiembrosConQuienesConvive()));
        ficha.getSeccionFamiliar().setAntecedentesPatologicosFamiliares(trimOrNull(request.getAntecedentesPatologicosFamiliares()));
        ficha.getSeccionFamiliar().setTieneAlgunaEnfermedad(trimOrNull(request.getTieneAlgunaEnfermedad()));
        ficha.getSeccionFamiliar().setTipoEnfermedad(trimOrNull(request.getTipoEnfermedad()));
        ficha.getSeccionFamiliar().setObservacion(trimOrNull(request.getObservacion()));
    }

    private void aplicarSeccionFuncionesPsicologicas(FichaPsicologica ficha, ExamenFuncionesPsicologicasDTO request) {
        if (ficha.getSeccionFuncionesPsicologicas() == null) {
            ficha.setSeccionFuncionesPsicologicas(new ExamenFuncionesPsicologicas());
        }
        ficha.getSeccionFuncionesPsicologicas().setOrientacion(trimOrNull(request.getOrientacion()));
        ficha.getSeccionFuncionesPsicologicas().setAtencion(trimOrNull(request.getAtencion()));
        ficha.getSeccionFuncionesPsicologicas().setSensopercepciones(trimOrNull(request.getSensopercepciones()));
        ficha.getSeccionFuncionesPsicologicas().setVoluntad(trimOrNull(request.getVoluntad()));
        ficha.getSeccionFuncionesPsicologicas().setJuicioRazonamiento(trimOrNull(request.getJuicioRazonamiento()));
        ficha.getSeccionFuncionesPsicologicas().setNutricion(trimOrNull(request.getNutricion()));
        ficha.getSeccionFuncionesPsicologicas().setSueno(trimOrNull(request.getSueno()));
        ficha.getSeccionFuncionesPsicologicas().setSexual(trimOrNull(request.getSexual()));
        ficha.getSeccionFuncionesPsicologicas().setPensamientoCurso(trimOrNull(request.getPensamientoCurso()));
        ficha.getSeccionFuncionesPsicologicas().setPensamientoEstructura(trimOrNull(request.getPensamientoEstructura()));
        ficha.getSeccionFuncionesPsicologicas().setPensamientoContenido(trimOrNull(request.getPensamientoContenido()));
        ficha.getSeccionFuncionesPsicologicas().setConcienciaEnfermedadTratamiento(trimOrNull(request.getConcienciaEnfermedadTratamiento()));
    }

    private void aplicarSeccionRasgosExamenes(FichaPsicologica ficha, RasgosPersonalidadExamenesDTO request) {
        if (ficha.getSeccionRasgosExamenes() == null) {
            ficha.setSeccionRasgosExamenes(new DiagnosticoRasgosExamenesPsicologicos());
        }
        ficha.getSeccionRasgosExamenes().setRasgo(trimOrNull(request.getRasgo()));
        ficha.getSeccionRasgosExamenes().setObservacion(trimOrNull(request.getObservacion()));
        ficha.getSeccionRasgosExamenes().setExamenesPsicologicos(trimOrNull(request.getExamenesPsicologicos()));
    }

    private void aplicarSeccionEtiopatogenicaPronostico(FichaPsicologica ficha, FormulacionEtiopatogenicaPronosticoDTO request) {
        if (ficha.getSeccionEtiopatogenicaPronostico() == null) {
            ficha.setSeccionEtiopatogenicaPronostico(new FormulacionEtiopatogenicaPronostico());
        }
        ficha.getSeccionEtiopatogenicaPronostico().setFactoresPredisponentes(trimOrNull(request.getFactoresPredisponentes()));
        ficha.getSeccionEtiopatogenicaPronostico().setFactoresDeterminantes(trimOrNull(request.getFactoresDeterminantes()));
        ficha.getSeccionEtiopatogenicaPronostico().setFactoresDesencadenantes(trimOrNull(request.getFactoresDesencadenantes()));
        ficha.getSeccionEtiopatogenicaPronostico().setPronosticoTipo(trimOrNull(request.getPronosticoTipo()));
    }

    // ============================================
    // MÉTODOS PRIVADOS EXISTENTES (sin cambios)
    // ============================================

    private CatalogoDiagnosticoCie10 resolverDiagnosticoCatalogo(FichaPsicologica ficha,
                                                                 CondicionClinicaEnum condicion,
                                                                 Long diagnosticoId,
                                                                 boolean solicitudObligatoria) {
        // Este método ya no se usa para diagnóstico único. Adaptar o eliminar según la nueva lógica de múltiples diagnósticos.
        throw new UnsupportedOperationException("La lógica de diagnóstico único ha sido reemplazada por múltiples diagnósticos.");
    }

    private void aplicarMetadatosCondicion(FichaPsicologica ficha,
                                           CondicionClinicaEnum condicion,
                                           LocalDate proximoSeguimientoSolicitado,
                                           String transferenciaUnidad,
                                           String transferenciaObservacion) {
        if (condicion == null) {
            return;
        }

        if (CondicionClinicaEnum.TRANSFERENCIA.equals(condicion)) {
            String unidad = trimOrNull(transferenciaUnidad);
            if (unidad == null) {
                throw new IllegalArgumentException("Debe registrar la unidad o lugar de transferencia");
            }
            TransferenciaInfo transferencia = Optional.ofNullable(ficha.getTransferenciaInfo())
                .orElseGet(TransferenciaInfo::new);
            transferencia.setUnidadDestino(unidad);
            transferencia.setObservacion(trimOrNull(transferenciaObservacion));
            transferencia.setFechaTransferencia(LocalDate.now());
            ficha.setTransferenciaInfo(transferencia);
            ficha.setProximoSeguimiento(null);
        } else {
            ficha.setTransferenciaInfo(null);
        }

        if (CondicionClinicaEnum.SEGUIMIENTO.equals(condicion) && proximoSeguimientoSolicitado != null) {
            ficha.setProximoSeguimiento(proximoSeguimientoSolicitado);
        } else if (!CondicionClinicaEnum.SEGUIMIENTO.equals(condicion)) {
            ficha.setProximoSeguimiento(null);
            if (!CondicionClinicaEnum.TRANSFERENCIA.equals(condicion)) {
                ficha.setUltimaFechaSeguimiento(null);
            }
        }
    }

    // Eliminado: método actualizarProgramacionSeguimiento relacionado a seguimientos

    private PlanSeguimiento buildPlan(FichaPsicologica ficha,
                                      CondicionClinicaEnum condicion,
                                      String planFrecuenciaRaw,
                                      String planTipoSesionRaw,
                                      String planDetalleRaw,
                                      boolean condicionObligatoria) {
        String frecuenciaToken = trimOrNull(planFrecuenciaRaw);
        String tipoSesionToken = trimOrNull(planTipoSesionRaw);
        String detalle = trimOrNull(planDetalleRaw);

        PlanSeguimiento existente = ficha.getPlanSeguimiento();

        if (frecuenciaToken == null && existente != null && existente.getFrecuencia() != null) {
            frecuenciaToken = existente.getFrecuencia().getCanonical();
        }

        if (tipoSesionToken == null && existente != null && existente.getTipoSesion() != null) {
            tipoSesionToken = existente.getTipoSesion().getCanonical();
        }

        if (detalle == null && existente != null) {
            detalle = existente.getDetalle();
        }

        // Para SEGUIMIENTO, frecuencia y tipo de sesión son obligatorios
        // Para TRANSFERENCIA, el plan es opcional (puede ser null)
        if (CondicionClinicaEnum.SEGUIMIENTO.equals(condicion)) {
            if (frecuenciaToken == null) {
                throw new IllegalArgumentException("La frecuencia del plan de seguimiento es obligatoria");
            }
            if (tipoSesionToken == null) {
                throw new IllegalArgumentException("El tipo de sesion es obligatorio");
            }
        } else if (CondicionClinicaEnum.TRANSFERENCIA.equals(condicion)) {
            // Para transferencia, si no hay frecuencia o tipo de sesión, no creamos plan
            if (frecuenciaToken == null || tipoSesionToken == null) {
                return null;
            }
        }

        if (frecuenciaToken == null || tipoSesionToken == null) {
            // Si llegamos aquí con tokens nulos, no se puede construir un plan coherente
            return null;
        }

        return PlanSeguimiento.builder()
            .frecuencia(FrecuenciaSeguimientoEnum.from(frecuenciaToken)
                .orElseThrow(() -> new IllegalArgumentException("Frecuencia de seguimiento no soportada: " + planFrecuenciaRaw)))
            .tipoSesion(TipoSesionEnum.from(tipoSesionToken)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de sesion no soportado: " + planTipoSesionRaw)))
            .detalle(detalle)
            .build();
    }

    private LocalDate calcularProximoSeguimiento(FichaPsicologica ficha,
                                                 LocalDate ultimaFechaSeguimiento,
                                                 LocalDate proximoSolicitado) {
        PlanSeguimiento plan = ficha.getPlanSeguimiento();
        if (plan == null || plan.getFrecuencia() == null) {
            return proximoSolicitado;
        }

        FrecuenciaSeguimientoEnum frecuencia = plan.getFrecuencia();
        if (FrecuenciaSeguimientoEnum.PERSONALIZADA.equals(frecuencia)) {
            if (proximoSolicitado == null) {
                throw new IllegalArgumentException("Debe especificar la próxima fecha de seguimiento para planes personalizados");
            }
            if (ultimaFechaSeguimiento != null && !proximoSolicitado.isAfter(ultimaFechaSeguimiento)) {
                throw new IllegalArgumentException("La próxima fecha de seguimiento debe ser posterior a la última registrada");
            }
            return proximoSolicitado;
        }

        LocalDate base = ultimaFechaSeguimiento != null ? ultimaFechaSeguimiento : ficha.getFechaEvaluacion();
        if (base == null) {
            base = LocalDate.now();
        }

        LocalDate calculada = switch (frecuencia) {
            case SEMANAL -> base.plusWeeks(1);
            case QUINCENAL -> base.plusDays(15);
            case MENSUAL -> base.plusMonths(1);
            case BIMESTRAL -> base.plusMonths(2);
            case TRIMESTRAL -> base.plusMonths(3);
            default -> base.plusWeeks(1);
        };

        if (proximoSolicitado != null && proximoSolicitado.isAfter(base)) {
            calculada = proximoSolicitado;
        }

        return calculada;
    }

    private void aplicarDatosGenerales(FichaPsicologica ficha, FichaDatosGeneralesRequestDTO request) {
        ficha.setFechaEvaluacion(normalizarFecha(request.getFechaEvaluacion()));
        ficha.setTipoEvaluacion(request.getTipoEvaluacion());
        ficha.setEstado(resolveEstadoRequired(request.getEstado()));
    }

    private void aplicarSeccionObservacion(FichaPsicologica ficha, FichaSeccionObservacionRequestDTO request) {
        if (ficha.getSeccionObservacion() == null) {
            ficha.setSeccionObservacion(new ObservacionClinica());
        }
        ficha.getSeccionObservacion().setObservacionClinica(request.getObservacionClinica().trim());
        ficha.getSeccionObservacion().setMotivoConsulta(request.getMotivoConsulta().trim());
        ficha.getSeccionObservacion().setEnfermedadActual(trimOrNull(request.getEnfermedadActual()));
        ficha.getSeccionObservacion().setHistoriaPasadaEnfermedad(mapHistoriaPasada(request.getHistoriaPasadaEnfermedad()));
    }

    private void aplicarSeccionPsicoanamnesis(FichaPsicologica ficha, FichaSeccionPsicoanamnesisRequestDTO request) {
        if (request.getPrenatal() != null) {
            if (ficha.getSeccionPrenatal() == null) {
                ficha.setSeccionPrenatal(new PsicoanamnesisPrenatal());
            }
            ficha.getSeccionPrenatal().setCondicionesBiologicasPadres(trimOrNull(request.getPrenatal().getCondicionesBiologicasPadres()));
            ficha.getSeccionPrenatal().setCondicionesPsicologicasPadres(trimOrNull(request.getPrenatal().getCondicionesPsicologicasPadres()));
            ficha.getSeccionPrenatal().setObservacion(trimOrNull(request.getPrenatal().getObservacion()));
        }

        if (request.getNatal() != null) {
            if (ficha.getSeccionNatal() == null) {
                ficha.setSeccionNatal(new PsicoanamnesisNatal());
            }
            ficha.getSeccionNatal().setPartoNormal(request.getNatal().getPartoNormal());
            ficha.getSeccionNatal().setTermino(trimOrNull(request.getNatal().getTermino()));
            ficha.getSeccionNatal().setComplicaciones(trimOrNull(request.getNatal().getComplicaciones()));
            ficha.getSeccionNatal().setObservacion(trimOrNull(request.getNatal().getObservacion()));
        }

        if (request.getInfancia() != null) {
            if (ficha.getSeccionInfancia() == null) {
                ficha.setSeccionInfancia(new PsicoanamnesisInfancia());
            }
            ficha.getSeccionInfancia().setGradoSociabilidad(resolveGradoSociabilidad(request.getInfancia().getGradoSociabilidad()));
            ficha.getSeccionInfancia().setRelacionPadresHermanos(resolveRelacionFamiliar(request.getInfancia().getRelacionPadresHermanos()));
            ficha.getSeccionInfancia().setDiscapacidadIntelectual(request.getInfancia().getDiscapacidadIntelectual());
            ficha.getSeccionInfancia().setGradoDiscapacidad(resolveGradoDiscapacidad(request.getInfancia().getGradoDiscapacidad()));
            ficha.getSeccionInfancia().setTrastornos(trimOrNull(request.getInfancia().getTrastornos()));
            ficha.getSeccionInfancia().setTratamientosPsicologicosPsiquiatricos(request.getInfancia().getTratamientosPsicologicosPsiquiatricos());
            ficha.getSeccionInfancia().setObservacion(trimOrNull(request.getInfancia().getObservacion()));
        }
    }

    private void validarFichaListaParaCierre(FichaPsicologica ficha) {
        CondicionClinicaEnum condicion = ficha.getCondicionClinica();
        if (condicion == null || !condicion.requierePlan()) {
            return;
        }

        if (ficha.getDiagnosticosCie10() == null || ficha.getDiagnosticosCie10().isEmpty()) {
            throw new IllegalStateException("Debe registrar al menos un diagnóstico CIE-10 antes de cerrar la ficha");
        }
        boolean tieneCodigo = ficha.getDiagnosticosCie10().stream()
            .anyMatch(d -> d.getCodigo() != null && !d.getCodigo().isBlank());
        if (!tieneCodigo) {
            throw new IllegalStateException("Al menos un diagnóstico CIE-10 debe tener código para cerrar la ficha");
        }

        PlanSeguimiento plan = ficha.getPlanSeguimiento();
        if (plan == null || plan.getFrecuencia() == null || plan.getTipoSesion() == null) {
            throw new IllegalStateException("Debe definir la frecuencia y el tipo de sesión del plan de seguimiento para cerrar la ficha");
        }
    }

    private String generarNumeroEvaluacionUnico() {
        String numero;
        do {
            numero = generarNumeroEvaluacion();
        } while (fichaPsicologicaRepository.existsByNumeroEvaluacion(numero));
        return numero;
    }

    private String generarNumeroEvaluacion() {
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return "FIC-" + LocalDate.now().toString().replaceAll("-", "") + "-" + token;
    }

    private LocalDate normalizarFecha(LocalDate fecha) {
        return fecha != null ? fecha : LocalDate.now();
    }

    private String trimOrNull(String value) {
        return value != null && !value.trim().isEmpty() ? value.trim() : null;
    }

    private HistoriaPasadaEnfermedad mapHistoriaPasada(ec.mil.dsndft.servicio_gestion.model.dto.HistoriaPasadaEnfermedadRequestDTO request) {
        if (request == null) {
            return null;
        }

        String descripcion = trimOrNull(request.getDescripcion());
        Boolean tomaMedicacion = request.getTomaMedicacion();
        String tipoMedicacion = trimOrNull(request.getTipoMedicacion());

        ec.mil.dsndft.servicio_gestion.model.dto.HospitalizacionRehabilitacionRequestDTO hospitalizacionRequest = request.getHospitalizacionRehabilitacion();

        HospitalizacionRehabilitacion hospitalizacion = null;
        if (hospitalizacionRequest != null) {
            Boolean requiere = hospitalizacionRequest.getRequiere();
            String tipo = trimOrNull(hospitalizacionRequest.getTipo());
            String duracion = trimOrNull(hospitalizacionRequest.getDuracion());

            if (requiere != null || tipo != null || duracion != null) {
                hospitalizacion = new HospitalizacionRehabilitacion();
                hospitalizacion.setRequiere(requiere);
                hospitalizacion.setTipo(tipo);
                hospitalizacion.setDuracion(duracion);
            }
        }

        if (descripcion == null && tomaMedicacion == null && tipoMedicacion == null && hospitalizacion == null) {
            return null;
        }

        HistoriaPasadaEnfermedad historia = new HistoriaPasadaEnfermedad();
        historia.setDescripcion(descripcion);
        historia.setTomaMedicacion(tomaMedicacion);
        historia.setTipoMedicacion(tipoMedicacion);
        historia.setHospitalizacionRehabilitacion(hospitalizacion);
        return historia;
    }

    // Método eliminado: resolveTipoEvaluacion

    private GradoSociabilidadEnum resolveGradoSociabilidad(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return GradoSociabilidadEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Grado de sociabilidad no soportado: " + raw));
    }

    private RelacionFamiliarEnum resolveRelacionFamiliar(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return RelacionFamiliarEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Relación familiar no soportada: " + raw));
    }

    private GradoDiscapacidadEnum resolveGradoDiscapacidad(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return GradoDiscapacidadEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Grado de discapacidad no soportado: " + raw));
    }

    private EstadoFichaEnum resolveEstadoRequired(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("El estado o condición es obligatorio");
        }
        return EstadoFichaEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Estado de ficha no soportado: " + raw));
    }

    private EstadoFichaEnum resolveEstadoOptional(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return EstadoFichaEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Estado de ficha no soportado: " + raw));
    }

    private CondicionClinicaEnum resolveCondicionOptional(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return CondicionClinicaEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Condición clínica no soportada: " + raw));
    }

    private CondicionClinicaEnum resolveCondicionRequired(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("La condición clínica es obligatoria");
        }
        return CondicionClinicaEnum.from(raw)
            .orElseThrow(() -> new IllegalArgumentException("Condición clínica no soportada: " + raw));
    }

    private Psicologo obtenerPsicologoAutenticado(Psicologo actual) {
        Psicologo psicologo = psicologoAutenticadoProvider.requireCurrent();
        if (actual != null && actual.getId() != null && !actual.getId().equals(psicologo.getId())) {
            throw new EntityNotFoundException("Psicólogo no coincide con el usuario autenticado");
        }
        return psicologo;
    }

    private Long ajustarFiltroPsicologo(Long solicitado) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return solicitado;
        }

        boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMINISTRADOR".equalsIgnoreCase(authority.getAuthority()));
        if (esAdmin) {
            return solicitado;
        }

        boolean esPsicologo = authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_PSICOLOGO".equalsIgnoreCase(authority.getAuthority()));
        if (!esPsicologo) {
            return solicitado;
        }

        Psicologo actual = psicologoAutenticadoProvider.requireCurrent();
        if (solicitado != null && !solicitado.equals(actual.getId())) {
            log.warn("El filtro de psicólogo {} no coincide con el autenticado ({}). Se aplicará el autenticado.", solicitado, actual.getId());
        }
        return actual.getId();
    }
}