package ec.mil.dsndft.servicio_gestion.service.impl;

import ec.mil.dsndft.servicio_gestion.entity.*;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.*;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10DTO;
import ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum;
import ec.mil.dsndft.servicio_gestion.repository.*;
import ec.mil.dsndft.servicio_gestion.service.ReporteGestionService;
import ec.mil.dsndft.servicio_gestion.service.support.AuthenticatedPsicologoProvider;
import ec.mil.dsndft.servicio_gestion.service.CatalogoDiagnosticoCie10Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteGestionServiceImpl implements ReporteGestionService {
    private final PsicologoRepository psicologoRepository;
    private final FichaPsicologicaRepository fichaPsicologicaRepository;
    private final AtencionPsicologicaRepository atencionRepository;
    private final AtencionPsicologicaHistorialRepository atencionHistorialRepository;
    private final AuthenticatedPsicologoProvider psicologoAutenticadoProvider;
    private final PersonalMilitarRepository personalMilitarRepository;
    private final CatalogoDiagnosticoCie10Service catalogoDiagnosticoCie10Service;

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteAtencionPsicologoDTO> obtenerAtencionesPorPsicologo(Long psicologoId,
                                                                           String psicologoCedula,
                                                                           LocalDate fechaDesde,
                                                                           LocalDate fechaHasta,
                                                                           Long diagnosticoId,
                                                                           String cedula,
                                                                           String unidadMilitar,
                                                                           Pageable pageable) {
        validarRangoFechas(fechaDesde, fechaHasta);
        Long psicologoFiltro = resolverPsicologoId(psicologoId, psicologoCedula);
        CatalogoDiagnosticoCie10DTO diagnostico = resolverDiagnostico(diagnosticoId);
        Long diagnosticoFiltro = diagnostico != null ? diagnostico.getId() : null;
        String unidadFiltro = normalizarFiltro(unidadMilitar);
        String cedulaFiltro = normalizarCedula(cedula);

        // Obtener todas las atenciones filtradas (por asignación de ficha)
        List<AtencionPsicologica> atenciones = atencionRepository.findAll().stream()
            .filter(a -> (psicologoFiltro == null || (a.getPsicologo() != null && psicologoFiltro.equals(a.getPsicologo().getId())))
                && (fechaDesde == null || (a.getFechaAtencion() != null && !a.getFechaAtencion().isBefore(fechaDesde)))
                && (fechaHasta == null || (a.getFechaAtencion() != null && !a.getFechaAtencion().isAfter(fechaHasta)))
                && (diagnosticoFiltro == null || (a.getDiagnosticos() != null && a.getDiagnosticos().stream().anyMatch(d -> diagnosticoFiltro.equals(d.getId()))))
                && (cedulaFiltro == null || (a.getPersonalMilitar() != null && cedulaFiltro.equalsIgnoreCase(a.getPersonalMilitar().getCedula())))
                && (unidadFiltro == null || (a.getPsicologo() != null && unidadFiltro.equalsIgnoreCase(a.getPsicologo().getUnidadMilitar())))
            )
            .collect(Collectors.toList());

        // Agrupar por psicólogo (asignación de la ficha) y contar por estado actual de la atención
        List<ReporteAtencionPsicologoDTO> reporte = atenciones.stream()
            .collect(Collectors.groupingBy(a -> a.getPsicologo() != null ? a.getPsicologo().getId() : null))
            .entrySet().stream()
            .map(entry -> {
                Long idPsicologo = entry.getKey();
                List<AtencionPsicologica> lista = entry.getValue();
                if (lista.isEmpty() || idPsicologo == null) return null;
                Psicologo psicologo = lista.get(0).getPsicologo();
                long totalAtenciones = lista.size();
                long programadas = lista.stream().filter(a -> "PROGRAMADA".equalsIgnoreCase(a.getEstado())).count();
                long enCurso = lista.stream().filter(a -> "EN_CURSO".equalsIgnoreCase(a.getEstado())).count();
                long finalizadas = lista.stream().filter(a -> "FINALIZADA".equalsIgnoreCase(a.getEstado())).count();
                long canceladas = lista.stream().filter(a -> "CANCELADA".equalsIgnoreCase(a.getEstado())).count();
                long noAsistio = lista.stream().filter(a -> "NO_ASISTIO".equalsIgnoreCase(a.getEstado())).count();
                long personasAtendidas = lista.stream().map(a -> a.getPersonalMilitar() != null ? a.getPersonalMilitar().getId() : null).distinct().count();
                LocalDate ultimaAtencion = lista.stream().map(AtencionPsicologica::getFechaAtencion).max(LocalDate::compareTo).orElse(null);

                ReporteAtencionPsicologoDTO dto = new ReporteAtencionPsicologoDTO();
                dto.setPsicologoId(idPsicologo);
                dto.setPsicologoNombre(psicologo.getApellidosNombres());
                dto.setPsicologoUsername(psicologo.getUsername());
                dto.setPsicologoUnidadMilitar(psicologo.getUnidadMilitar());
                dto.setTotalFichasAtendidas(totalAtenciones); // Puede renombrarse a totalAtenciones
                dto.setFichasActivas(programadas); // PROGRAMADA
                dto.setFichasObservacion(enCurso); // EN_CURSO
                dto.setTotalSeguimientos(finalizadas); // FINALIZADA
                // Si quieres mostrar canceladas y no asistió, puedes agregar campos en el DTO
                dto.setPersonasAtendidas(personasAtendidas);
                dto.setUltimaAtencion(ultimaAtencion);
                dto.setFiltroDiagnosticoId(diagnosticoFiltro);
                dto.setFiltroDiagnosticoCodigo(diagnostico != null ? diagnostico.getCodigo() : null);
                dto.setFiltroDiagnosticoTexto(diagnostico != null ? diagnostico.getDescripcion() : null);
                dto.setFiltroCedula(cedulaFiltro);
                dto.setFiltroUnidadMilitar(unidadFiltro);
                dto.setFiltroFechaDesde(fechaDesde);
                dto.setFiltroFechaHasta(fechaHasta);
                return dto;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());

        // -------- Estadísticas adicionales basadas en historial (acciones por psicólogo) --------
        List<AtencionPsicologicaHistorial> historiales = atencionHistorialRepository.findAll();

        for (AtencionPsicologicaHistorial historial : historiales) {
            Psicologo actor = historial.getPsicologo();
            AtencionPsicologica atencion = historial.getAtencion();
            if (actor == null || atencion == null) {
                continue;
            }

            Long actorId = actor.getId();
            if (actorId == null) {
                continue;
            }

            // Aplicar mismos filtros que al listado principal, pero respecto al psicólogo actor
            if (psicologoFiltro != null && !psicologoFiltro.equals(actorId)) {
                continue;
            }

            if (fechaDesde != null && (atencion.getFechaAtencion() == null || atencion.getFechaAtencion().isBefore(fechaDesde))) {
                continue;
            }
            if (fechaHasta != null && (atencion.getFechaAtencion() == null || atencion.getFechaAtencion().isAfter(fechaHasta))) {
                continue;
            }

            if (diagnosticoFiltro != null) {
                boolean coincideDiagnostico = atencion.getDiagnosticos() != null &&
                    atencion.getDiagnosticos().stream().anyMatch(d -> diagnosticoFiltro.equals(d.getId()));
                if (!coincideDiagnostico) {
                    continue;
                }
            }

            if (cedulaFiltro != null) {
                if (atencion.getPersonalMilitar() == null ||
                    atencion.getPersonalMilitar().getCedula() == null ||
                    !cedulaFiltro.equalsIgnoreCase(atencion.getPersonalMilitar().getCedula())) {
                    continue;
                }
            }

            if (unidadFiltro != null) {
                String unidadActor = actor.getUnidadMilitar();
                if (unidadActor == null || !unidadFiltro.equalsIgnoreCase(unidadActor)) {
                    continue;
                }
            }

            // Buscar/crear entrada de reporte para este psicólogo (actor)
            ReporteAtencionPsicologoDTO dto = reporte.stream()
                .filter(r -> actorId.equals(r.getPsicologoId()))
                .findFirst()
                .orElseGet(() -> {
                    ReporteAtencionPsicologoDTO nuevo = new ReporteAtencionPsicologoDTO();
                    nuevo.setPsicologoId(actorId);
                    nuevo.setPsicologoNombre(actor.getApellidosNombres());
                    nuevo.setPsicologoUsername(actor.getUsername());
                    nuevo.setPsicologoUnidadMilitar(actor.getUnidadMilitar());
                    nuevo.setTotalFichasAtendidas(0L);
                    nuevo.setFichasActivas(0L);
                    nuevo.setFichasObservacion(0L);
                    nuevo.setTotalSeguimientos(0L);
                    nuevo.setPersonasAtendidas(0L);
                    reporte.add(nuevo);
                    return nuevo;
                });

            String estado = historial.getEstado() != null
                ? historial.getEstado().toUpperCase(java.util.Locale.ROOT)
                : "";

            if ("PROGRAMADA".equals(estado)) {
                dto.setTotalAccionesProgramadas(
                    (dto.getTotalAccionesProgramadas() == null ? 0L : dto.getTotalAccionesProgramadas()) + 1
                );
            } else if ("EN_CURSO".equals(estado)) {
                dto.setTotalAccionesEnCurso(
                    (dto.getTotalAccionesEnCurso() == null ? 0L : dto.getTotalAccionesEnCurso()) + 1
                );
            } else if ("FINALIZADA".equals(estado)) {
                dto.setTotalAccionesFinalizadas(
                    (dto.getTotalAccionesFinalizadas() == null ? 0L : dto.getTotalAccionesFinalizadas()) + 1
                );
            } else if ("CANCELADA".equals(estado)) {
                dto.setTotalAccionesCanceladas(
                    (dto.getTotalAccionesCanceladas() == null ? 0L : dto.getTotalAccionesCanceladas()) + 1
                );
            } else if ("NO_ASISTIO".equals(estado)) {
                dto.setTotalAccionesNoAsistio(
                    (dto.getTotalAccionesNoAsistio() == null ? 0L : dto.getTotalAccionesNoAsistio()) + 1
                );
            }
        }

        return paginarEnMemoria(reporte, pageable);
    }

    // Método obtenerPersonasEnSeguimientoOTransferencia eliminado porque la funcionalidad de seguimientos fue removida del sistema.

    @Override
    @Transactional(readOnly = true)
    public Page<ReportePersonalDiagnosticoDTO> obtenerReportePersonalDiagnostico(LocalDate fechaDesde,
                                                                                 LocalDate fechaHasta,
                                                                                 Long diagnosticoId,
                                                                                 String cedula,
                                                                                 String grado,
                                                                                 String unidadMilitar,
                                                                                 Pageable pageable) {
        validarRangoFechas(fechaDesde, fechaHasta);
        CatalogoDiagnosticoCie10DTO diagnostico = resolverDiagnostico(diagnosticoId);
        Long diagnosticoFiltro = diagnostico != null ? diagnostico.getId() : null;
        String gradoFiltro = normalizarFiltro(grado);
        String unidadFiltro = normalizarFiltro(unidadMilitar);
        String cedulaFiltro = normalizarCedula(cedula);

        // RF017 - YA FUNCIONA CORRECTAMENTE
        List<ReportePersonalDiagnosticoDTO> resultado = fichaPsicologicaRepository
            .obtenerReportePersonalDiagnostico(fechaDesde, fechaHasta, diagnosticoFiltro, cedulaFiltro, gradoFiltro, unidadFiltro);

        resultado.forEach(dto -> {
            dto.setFiltroFechaDesde(fechaDesde);
            dto.setFiltroFechaHasta(fechaHasta);
            dto.setFiltroDiagnosticoId(diagnosticoFiltro);
            dto.setFiltroDiagnosticoCodigo(diagnostico != null ? diagnostico.getCodigo() : null);
            dto.setFiltroDiagnosticoTexto(diagnostico != null ? diagnostico.getDescripcion() : null);
            dto.setFiltroCedula(cedulaFiltro);
            dto.setFiltroGrado(gradoFiltro);
            dto.setFiltroUnidadMilitar(unidadFiltro);
        });

        return paginarEnMemoria(resultado, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteHistorialFichaDTO> obtenerHistorialFichas(Long personalMilitarId,
                                                                 String cedula,
                                                                 boolean incluirSeguimientos,
                                                                 Pageable pageable) {
        PersonalMilitar personal = resolverPersonal(personalMilitarId, cedula);
        Long personalId = personal.getId();
        String cedulaPersonal = personal.getCedula() != null ? personal.getCedula() : normalizarCedula(cedula);

        List<ReporteHistorialFichaDTO> registros = new ArrayList<>();

        // RF018 - PASO 1: OBTENER FICHAS ACTUALES DEL REPOSITORIO PRINCIPAL
        List<FichaPsicologica> fichasActuales = fichaPsicologicaRepository
            .findByPersonalMilitarIdOrderByFechaEvaluacionDesc(personalId);
        for (FichaPsicologica ficha : fichasActuales) {
            registros.add(construirDTODesdeFichaActual(ficha, personal, cedulaPersonal, incluirSeguimientos));
        }

                // Eliminado: obtención de fichas históricas

        // RF018 - PASO 3: ORDENAR POR FECHA DE EVALUACIÓN (MÁS RECIENTE PRIMERO)
        List<ReporteHistorialFichaDTO> sorted = registros.stream()
            .sorted(Comparator.comparing(ReporteHistorialFichaDTO::getFechaEvaluacion,
                Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(ReporteHistorialFichaDTO::getNumeroFicha, Comparator.nullsLast(String::compareTo)))
            .collect(Collectors.toList());
        return paginarEnMemoria(sorted, pageable);
    }

    // MÉTODO AUXILIAR PARA CONSTRUIR DTO DESDE FICHA ACTUAL
    private ReporteHistorialFichaDTO construirDTODesdeFichaActual(FichaPsicologica ficha, 
                                                                  PersonalMilitar personal,
                                                                  String cedulaFiltro,
                                                                  boolean incluirSeguimientos) {
        ReporteHistorialFichaDTO dto = new ReporteHistorialFichaDTO();
        dto.setOrigen("ACTUAL");
        dto.setPersonalMilitarId(personal.getId());

        // Usar datos del personal de la ficha o del personal principal
        PersonalMilitar personalFicha = ficha.getPersonalMilitar();
        if (personalFicha != null) {
            dto.setPersonalMilitarCedula(personalFicha.getCedula());
            dto.setPersonalMilitarNombre(personalFicha.getApellidosNombres());
        } else {
            dto.setPersonalMilitarCedula(personal.getCedula());
            dto.setPersonalMilitarNombre(personal.getApellidosNombres());
        }

        dto.setFichaId(ficha.getId());
        dto.setNumeroFicha(ficha.getNumeroEvaluacion());
        dto.setFechaEvaluacion(ficha.getFechaEvaluacion());
        dto.setEstadoFicha(ficha.getEstado() != null ? ficha.getEstado().getCanonical() : null);
        dto.setCondicionClinica(ficha.getCondicionClinica() != null ? ficha.getCondicionClinica().getCanonical() : null);

        // Adaptación: mostrar todos los diagnósticos CIE-10 (concatenados)
        if (ficha.getDiagnosticosCie10() != null && !ficha.getDiagnosticosCie10().isEmpty()) {
            String codigos = ficha.getDiagnosticosCie10().stream()
                .map(CatalogoDiagnosticoCie10::getCodigo)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.joining(", "));
            String nombres = ficha.getDiagnosticosCie10().stream()
                .map(CatalogoDiagnosticoCie10::getNombre)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.joining(", "));
            String categorias = ficha.getDiagnosticosCie10().stream()
                .map(CatalogoDiagnosticoCie10::getCategoriaPadre)
                .filter(cat -> cat != null && !cat.isBlank())
                .collect(Collectors.joining(", "));
            String niveles = ficha.getDiagnosticosCie10().stream()
                .map(d -> d.getNivel() != null ? d.getNivel().toString() : "")
                .filter(n -> !n.isBlank())
                .collect(Collectors.joining(", "));
            String descripciones = ficha.getDiagnosticosCie10().stream()
                .map(d -> d.getDescripcion() != null ? d.getDescripcion() : "")
                .filter(desc -> !desc.isBlank())
                .collect(Collectors.joining(" | "));
            dto.setDiagnosticoCodigo(codigos);
            dto.setDiagnosticoNombre(nombres);
            dto.setDiagnosticoCategoriaPadre(categorias);
            dto.setDiagnosticoNivel(!niveles.isBlank() ? Integer.valueOf(niveles.split(", ")[0]) : null); // Solo el primer nivel
            dto.setDiagnosticoDescripcion(descripciones);
        }

        // Datos del psicólogo
        if (ficha.getPsicologo() != null) {
            dto.setPsicologoId(ficha.getPsicologo().getId());
            dto.setPsicologoNombre(ficha.getPsicologo().getApellidosNombres());
            dto.setPsicologoUnidadMilitar(ficha.getPsicologo().getUnidadMilitar());
        }

        // RF018 - SEGUIMIENTOS: Incluir solo si se solicita
        if (incluirSeguimientos && ficha.getId() != null) {
            // Contar atenciones de seguimiento asociadas a ESTA ficha
            long totalSeguimientos = atencionRepository.findAll().stream()
                .filter(a -> a.getFichaPsicologica() != null
                        && ficha.getId().equals(a.getFichaPsicologica().getId())
                        && "SEGUIMIENTO".equalsIgnoreCase(a.getTipoConsulta()))
                .count();
            dto.setTotalSeguimientos(totalSeguimientos);
        }

        // Datos de filtro para el reporte
        dto.setFiltroPersonalMilitarId(personal.getId());
        dto.setFiltroCedula(cedulaFiltro);
        dto.setFiltroIncluirSeguimientos(incluirSeguimientos);

        return dto;
    }

    // Eliminado: método auxiliar para fichas históricas y seguimientos

    // MÉTODOS PRIVADOS DE SOPORTE (SE MANTIENEN IGUAL)
    private void validarRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    private CatalogoDiagnosticoCie10DTO resolverDiagnostico(Long diagnosticoId) {
        if (diagnosticoId == null) {
            return null;
        }
        return catalogoDiagnosticoCie10Service.obtenerPorId(diagnosticoId);
    }

    private PersonalMilitar resolverPersonal(Long personalMilitarId, String cedula) {
        if (personalMilitarId != null) {
            PersonalMilitar personal = personalMilitarRepository.findById(personalMilitarId)
                .orElseThrow(() -> new EntityNotFoundException("Personal militar no encontrado"));
            String cedulaNormalizada = normalizarCedula(cedula);
            if (cedulaNormalizada != null && personal.getCedula() != null
                && !personal.getCedula().equalsIgnoreCase(cedulaNormalizada)) {
                throw new IllegalArgumentException("La cédula indicada no coincide con el registro del personal");
            }
            return personal;
        }

        String cedulaNormalizada = normalizarCedula(cedula);
        if (cedulaNormalizada == null) {
            throw new IllegalArgumentException("Debe proporcionar el identificador del personal o la cédula");
        }

        return personalMilitarRepository.findByCedulaIgnoreCase(cedulaNormalizada)
            .orElseThrow(() -> new EntityNotFoundException("Personal militar no encontrado para la cédula indicada"));
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

        return psicologoAutenticadoProvider.findCurrent()
            .map(psicologo -> {
                if (solicitado != null && !solicitado.equals(psicologo.getId())) {
                    log.warn("El filtro de psicólogo {} no coincide con el autenticado ({}). Se aplicará el autenticado.", solicitado, psicologo.getId());
                }
                return psicologo.getId();
            })
            .orElse(solicitado);
    }

    private String normalizarFiltro(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizarCedula(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private Long resolverPsicologoId(Long psicologoId, String psicologoCedula) {
        Long idBase = psicologoId;
        String cedulaNormalizada = normalizarCedula(psicologoCedula);
        if (cedulaNormalizada != null) {
            Psicologo psicologo = psicologoRepository.findByCedula(cedulaNormalizada)
                .orElseThrow(() -> new EntityNotFoundException("Psicologo no encontrado para la cedula indicada"));
            idBase = psicologo.getId();
        }
        return ajustarFiltroPsicologo(idBase);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteSeguimientoTransferenciaDTO> obtenerReporteSeguimientoTransferencia(Long psicologoId,
                                                                                          String psicologoCedula,
                                                                                          String cedula,
                                                                                          String unidadMilitar,
                                                                                          LocalDate fechaDesde,
                                                                                          LocalDate fechaHasta,
                                                                                          boolean incluirSeguimientos,
                                                                                          Pageable pageable) {
        // Normalizar filtros
        String cedulaFiltro = normalizarCedula(cedula);
        String unidadFiltro = normalizarFiltro(unidadMilitar);
        Long psicologoFiltro = resolverPsicologoId(psicologoId, psicologoCedula);

        // Buscar fichas con condición clínica SEGUIMIENTO o TRANSFERENCIA
        List<FichaPsicologica> fichas = fichaPsicologicaRepository.findAll().stream()
            .filter(f -> (psicologoFiltro == null || (f.getPsicologo() != null && psicologoFiltro.equals(f.getPsicologo().getId())))
                && (cedulaFiltro == null || (f.getPersonalMilitar() != null && cedulaFiltro.equalsIgnoreCase(f.getPersonalMilitar().getCedula())))
                && (unidadFiltro == null || (f.getPsicologo() != null && unidadFiltro.equalsIgnoreCase(f.getPsicologo().getUnidadMilitar())))
                && (fechaDesde == null || (f.getFechaEvaluacion() != null && !f.getFechaEvaluacion().isBefore(fechaDesde)))
                && (fechaHasta == null || (f.getFechaEvaluacion() != null && !f.getFechaEvaluacion().isAfter(fechaHasta)))
                && (f.getCondicionClinica() == CondicionClinicaEnum.SEGUIMIENTO || f.getCondicionClinica() == CondicionClinicaEnum.TRANSFERENCIA)
            )
            .collect(Collectors.toList());

        List<ReporteSeguimientoTransferenciaDTO> resultado = new ArrayList<>();
        for (FichaPsicologica ficha : fichas) {
            Long totalSeguimientos = 0L;
            LocalDate ultimaFechaSeguimiento = null;
            if (incluirSeguimientos && ficha.getPersonalMilitar() != null) {
                List<AtencionPsicologica> atenciones = atencionRepository
                    .findByPersonalMilitarId(ficha.getPersonalMilitar().getId(), Pageable.unpaged())
                    .getContent();
                totalSeguimientos = atenciones.stream()
                    .filter(a -> "SEGUIMIENTO".equalsIgnoreCase(a.getTipoConsulta())
                        && a.getFichaPsicologica() != null
                        && ficha.getId().equals(a.getFichaPsicologica().getId()))
                    .count();
                ultimaFechaSeguimiento = atenciones.stream()
                    .filter(a -> "SEGUIMIENTO".equalsIgnoreCase(a.getTipoConsulta())
                        && a.getFichaPsicologica() != null
                        && ficha.getId().equals(a.getFichaPsicologica().getId()))
                    .map(a -> a.getFechaAtencion())
                    .filter(java.util.Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElse(null);
            }
            ReporteSeguimientoTransferenciaDTO dto = new ReporteSeguimientoTransferenciaDTO(
                ficha.getPersonalMilitar() != null ? ficha.getPersonalMilitar().getId() : null,
                ficha.getPersonalMilitar() != null ? ficha.getPersonalMilitar().getApellidosNombres() : null,
                ficha.getPersonalMilitar() != null ? ficha.getPersonalMilitar().getCedula() : null,
                ficha.getId(),
                ficha.getNumeroEvaluacion(),
                ficha.getCondicionClinica(),
                ficha.getPsicologo() != null ? ficha.getPsicologo().getId() : null,
                ficha.getPsicologo() != null ? ficha.getPsicologo().getApellidosNombres() : null,
                ficha.getPsicologo() != null ? ficha.getPsicologo().getUnidadMilitar() : null,
                totalSeguimientos,
                ultimaFechaSeguimiento,
                ficha.getFechaEvaluacion(),
                ficha.getTransferenciaInfo() != null ? ficha.getTransferenciaInfo().getUnidadDestino() : null,
                ficha.getTransferenciaInfo() != null ? ficha.getTransferenciaInfo().getObservacion() : null,
                ficha.getTransferenciaInfo() != null ? ficha.getTransferenciaInfo().getFechaTransferencia() : null,
                ficha.getProximoSeguimiento()
            );
            dto.setFiltroCedula(cedulaFiltro);
            dto.setFiltroUnidadMilitar(unidadFiltro);
            dto.setFiltroFechaDesde(fechaDesde);
            dto.setFiltroFechaHasta(fechaHasta);
            dto.setCondicionClinicaCanonical(ficha.getCondicionClinica() != null ? ficha.getCondicionClinica().getCanonical() : null);
            resultado.add(dto);
        }
        return paginarEnMemoria(resultado, pageable);

    }

    // Utilidad para paginar listas en memoria
    private <T> Page<T> paginarEnMemoria(List<T> lista, Pageable pageable) {
        int total = lista.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<T> sublist = (start > end) ? new ArrayList<>() : lista.subList(start, end);
        return new PageImpl<>(sublist, pageable, total);
    }
}