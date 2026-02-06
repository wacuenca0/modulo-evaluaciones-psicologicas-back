package ec.mil.dsndft.servicio_gestion.repository;

import ec.mil.dsndft.servicio_gestion.entity.FichaPsicologica;
import ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReporteSeguimientoTransferenciaDTO;
import ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.EstadoFichaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FichaPsicologicaRepository extends JpaRepository<FichaPsicologica, Long> {

    // Método existente de filtros
    @Query("SELECT f FROM FichaPsicologica f " +
           "WHERE (:psicologoId IS NULL OR f.psicologo.id = :psicologoId) " +
           "AND (:personalMilitarId IS NULL OR f.personalMilitar.id = :personalMilitarId) " +
           "AND (:estado IS NULL OR f.estado = :estado) " +
           "AND (:condicionClinica IS NULL OR f.condicionClinica = :condicionClinica) " +
           "AND (:soloActivas IS NULL OR (:soloActivas = TRUE AND f.estado = ec.mil.dsndft.servicio_gestion.model.enums.EstadoFichaEnum.ABIERTA)) " +
           "ORDER BY f.fechaEvaluacion DESC, f.createdAt DESC")
    List<FichaPsicologica> findByFilters(@Param("psicologoId") Long psicologoId,
                                         @Param("personalMilitarId") Long personalMilitarId,
                                         @Param("estado") EstadoFichaEnum estado,
                                         @Param("condicionClinica") CondicionClinicaEnum condicionClinica,
                                         @Param("soloActivas") Boolean soloActivas);

    // Métodos existentes para reportes

    @Query("SELECT new ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReportePersonalDiagnosticoDTO(" +
           "pm.id, " +
           "pm.cedula, " +
           "pm.apellidosNombres, " +
           "pm.tipoPersona, " +
           "pm.esMilitar, " +
           "pm.grado, " +
           "pm.unidadMilitar, " +
           "f.numeroEvaluacion, " +
           "f.fechaEvaluacion, " +
           "(SELECT d.codigo FROM f.diagnosticosCie10 d WHERE d.id = (SELECT MIN(dd.id) FROM f.diagnosticosCie10 dd)), " +
           "(SELECT d.nombre FROM f.diagnosticosCie10 d WHERE d.id = (SELECT MIN(dd.id) FROM f.diagnosticosCie10 dd)), " +
           "(SELECT d.categoriaPadre FROM f.diagnosticosCie10 d WHERE d.id = (SELECT MIN(dd.id) FROM f.diagnosticosCie10 dd)), " +
           "(SELECT d.nivel FROM f.diagnosticosCie10 d WHERE d.id = (SELECT MIN(dd.id) FROM f.diagnosticosCie10 dd)), " +
           "(SELECT d.descripcion FROM f.diagnosticosCie10 d WHERE d.id = (SELECT MIN(dd.id) FROM f.diagnosticosCie10 dd)), " +
           "psi.id, " +
           "psi.apellidosNombres, " +
           "psi.unidadMilitar" +
           ") " +
           "FROM FichaPsicologica f " +
           "JOIN f.personalMilitar pm " +
           "LEFT JOIN f.psicologo psi " +
           "WHERE pm.activo = true " +
           "  AND pm.tipoPersona IN ('Militar', 'Dependiente') " +
           "  AND (:fechaDesde IS NULL OR f.fechaEvaluacion >= :fechaDesde) " +
           "  AND (:fechaHasta IS NULL OR f.fechaEvaluacion <= :fechaHasta) " +
           "  AND (:diagnosticoId IS NULL OR EXISTS (SELECT d FROM f.diagnosticosCie10 d WHERE d.id = :diagnosticoId)) " +
           "  AND (:cedula IS NULL OR (pm.cedula IS NOT NULL AND UPPER(pm.cedula) = UPPER(:cedula))) " +
           "  AND (:grado IS NULL OR (pm.grado IS NOT NULL AND UPPER(pm.grado) = UPPER(:grado))) " +
           "  AND (:unidadMilitar IS NULL OR (pm.unidadMilitar IS NOT NULL AND UPPER(pm.unidadMilitar) = UPPER(:unidadMilitar))) " +
           "ORDER BY pm.apellidosNombres ASC, f.fechaEvaluacion DESC, f.numeroEvaluacion ASC")
    List<ec.mil.dsndft.servicio_gestion.model.dto.reportes.ReportePersonalDiagnosticoDTO> obtenerReportePersonalDiagnostico(@Param("fechaDesde") LocalDate fechaDesde,
                                                                                                                            @Param("fechaHasta") LocalDate fechaHasta,
                                                                                                                            @Param("diagnosticoId") Long diagnosticoId,
                                                                                                                            @Param("cedula") String cedula,
                                                                                                                            @Param("grado") String grado,
                                                                                                                            @Param("unidadMilitar") String unidadMilitar);

    // Métodos existentes
    List<FichaPsicologica> findByPersonalMilitarIdOrderByFechaEvaluacionDesc(Long personalMilitarId);
    boolean existsByNumeroEvaluacion(String numeroEvaluacion);
    Optional<FichaPsicologica> findByNumeroEvaluacion(String numeroEvaluacion);
    
    // Historial paginado por personal con filtros opcionales de cédula del psicólogo y rango de fechas
    @Query("SELECT f FROM FichaPsicologica f " +
          "WHERE f.personalMilitar.id = :personalMilitarId " +
          "AND (:cedulaPsicologo IS NULL OR (f.psicologo IS NOT NULL AND UPPER(f.psicologo.cedula) = UPPER(:cedulaPsicologo))) " +
          "AND (:fechaDesde IS NULL OR f.fechaEvaluacion >= :fechaDesde) " +
          "AND (:fechaHasta IS NULL OR f.fechaEvaluacion <= :fechaHasta) " +
          "ORDER BY f.fechaEvaluacion DESC, f.createdAt DESC")
    org.springframework.data.domain.Page<FichaPsicologica> findHistorialByFilters(
           @Param("personalMilitarId") Long personalMilitarId,
           @Param("cedulaPsicologo") String cedulaPsicologo,
           @Param("fechaDesde") java.time.LocalDate fechaDesde,
           @Param("fechaHasta") java.time.LocalDate fechaHasta,
           org.springframework.data.domain.Pageable pageable);
    
    // NUEVOS MÉTODOS para actualizar las nuevas secciones
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionAdolescencia.habilidadesSociales = :habilidadesSociales, " +
           "f.seccionAdolescencia.trastorno = :trastorno, " +
           "f.seccionAdolescencia.historiaPersonal = :historiaPersonal, " +
           "f.seccionAdolescencia.maltratoAdultoProblemasNegligencia = :maltratoAdulto, " +
           "f.seccionAdolescencia.problemasRelacionadosCircunstanciasLegales = :problemasLegales, " +
           "f.seccionAdolescencia.tratamientosPsicologicosPsiquiatricos = :tratamientos, " +
           "f.seccionAdolescencia.observacion = :observacion, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int updateSeccionAdolescencia(@Param("id") Long id,
                                  @Param("habilidadesSociales") String habilidadesSociales,
                                  @Param("trastorno") String trastorno,
                                  @Param("historiaPersonal") String historiaPersonal,
                                  @Param("maltratoAdulto") String maltratoAdulto,
                                  @Param("problemasLegales") String problemasLegales,
                                  @Param("tratamientos") String tratamientos,
                                  @Param("observacion") String observacion);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionFamiliar.miembrosConQuienesConvive = :miembrosConQuienesConvive, " +
           "f.seccionFamiliar.antecedentesPatologicosFamiliares = :antecedentesPatologicos, " +
           "f.seccionFamiliar.tieneAlgunaEnfermedad = :tieneEnfermedad, " +
           "f.seccionFamiliar.tipoEnfermedad = :tipoEnfermedad, " +
           "f.seccionFamiliar.observacion = :observacion, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int updateSeccionFamiliar(@Param("id") Long id,
                             @Param("miembrosConQuienesConvive") String miembrosConQuienesConvive,
                             @Param("antecedentesPatologicos") String antecedentesPatologicos,
                             @Param("tieneEnfermedad") String tieneEnfermedad,
                             @Param("tipoEnfermedad") String tipoEnfermedad,
                             @Param("observacion") String observacion);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionFuncionesPsicologicas.orientacion = :orientacion, " +
           "f.seccionFuncionesPsicologicas.atencion = :atencion, " +
           "f.seccionFuncionesPsicologicas.sensopercepciones = :sensopercepciones, " +
           "f.seccionFuncionesPsicologicas.voluntad = :voluntad, " +
           "f.seccionFuncionesPsicologicas.juicioRazonamiento = :juicioRazonamiento, " +
           "f.seccionFuncionesPsicologicas.nutricion = :nutricion, " +
           "f.seccionFuncionesPsicologicas.sueno = :sueno, " +
           "f.seccionFuncionesPsicologicas.sexual = :sexual, " +
           "f.seccionFuncionesPsicologicas.pensamientoCurso = :pensamientoCurso, " +
           "f.seccionFuncionesPsicologicas.pensamientoEstructura = :pensamientoEstructura, " +
           "f.seccionFuncionesPsicologicas.pensamientoContenido = :pensamientoContenido, " +
           "f.seccionFuncionesPsicologicas.concienciaEnfermedadTratamiento = :concienciaEnfermedad, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int updateSeccionFuncionesPsicologicas(@Param("id") Long id,
                                           @Param("orientacion") String orientacion,
                                           @Param("atencion") String atencion,
                                           @Param("sensopercepciones") String sensopercepciones,
                                           @Param("voluntad") String voluntad,
                                           @Param("juicioRazonamiento") String juicioRazonamiento,
                                           @Param("nutricion") String nutricion,
                                           @Param("sueno") String sueno,
                                           @Param("sexual") String sexual,
                                           @Param("pensamientoCurso") String pensamientoCurso,
                                           @Param("pensamientoEstructura") String pensamientoEstructura,
                                           @Param("pensamientoContenido") String pensamientoContenido,
                                           @Param("concienciaEnfermedad") String concienciaEnfermedad);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionRasgosExamenes.rasgo = :rasgo, " +
           "f.seccionRasgosExamenes.observacion = :observacion, " +
           "f.seccionRasgosExamenes.examenesPsicologicos = :examenesPsicologicos, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int updateSeccionRasgosExamenes(@Param("id") Long id,
                                    @Param("rasgo") String rasgo,
                                    @Param("observacion") String observacion,
                                    @Param("examenesPsicologicos") String examenesPsicologicos);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionEtiopatogenicaPronostico.factoresPredisponentes = :factoresPredisponentes, " +
           "f.seccionEtiopatogenicaPronostico.factoresDeterminantes = :factoresDeterminantes, " +
           "f.seccionEtiopatogenicaPronostico.factoresDesencadenantes = :factoresDesencadenantes, " +
           "f.seccionEtiopatogenicaPronostico.pronosticoTipo = :pronosticoTipo, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int updateSeccionEtiopatogenicaPronostico(@Param("id") Long id,
                                              @Param("factoresPredisponentes") String factoresPredisponentes,
                                              @Param("factoresDeterminantes") String factoresDeterminantes,
                                              @Param("factoresDesencadenantes") String factoresDesencadenantes,
                                              @Param("pronosticoTipo") String pronosticoTipo);
    
    // Métodos para eliminar secciones específicas
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionAdolescencia.habilidadesSociales = NULL, " +
           "f.seccionAdolescencia.trastorno = NULL, " +
           "f.seccionAdolescencia.historiaPersonal = NULL, " +
           "f.seccionAdolescencia.maltratoAdultoProblemasNegligencia = NULL, " +
           "f.seccionAdolescencia.problemasRelacionadosCircunstanciasLegales = NULL, " +
           "f.seccionAdolescencia.tratamientosPsicologicosPsiquiatricos = NULL, " +
           "f.seccionAdolescencia.observacion = NULL, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int limpiarSeccionAdolescencia(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionFamiliar.miembrosConQuienesConvive = NULL, " +
           "f.seccionFamiliar.antecedentesPatologicosFamiliares = NULL, " +
           "f.seccionFamiliar.tieneAlgunaEnfermedad = NULL, " +
           "f.seccionFamiliar.tipoEnfermedad = NULL, " +
           "f.seccionFamiliar.observacion = NULL, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int limpiarSeccionFamiliar(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionFuncionesPsicologicas.orientacion = NULL, " +
           "f.seccionFuncionesPsicologicas.atencion = NULL, " +
           "f.seccionFuncionesPsicologicas.sensopercepciones = NULL, " +
           "f.seccionFuncionesPsicologicas.voluntad = NULL, " +
           "f.seccionFuncionesPsicologicas.juicioRazonamiento = NULL, " +
           "f.seccionFuncionesPsicologicas.nutricion = NULL, " +
           "f.seccionFuncionesPsicologicas.sueno = NULL, " +
           "f.seccionFuncionesPsicologicas.sexual = NULL, " +
           "f.seccionFuncionesPsicologicas.pensamientoCurso = NULL, " +
           "f.seccionFuncionesPsicologicas.pensamientoEstructura = NULL, " +
           "f.seccionFuncionesPsicologicas.pensamientoContenido = NULL, " +
           "f.seccionFuncionesPsicologicas.concienciaEnfermedadTratamiento = NULL, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int limpiarSeccionFuncionesPsicologicas(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionRasgosExamenes.rasgo = NULL, " +
           "f.seccionRasgosExamenes.observacion = NULL, " +
           "f.seccionRasgosExamenes.examenesPsicologicos = NULL, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int limpiarSeccionRasgosExamenes(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE FichaPsicologica f SET " +
           "f.seccionEtiopatogenicaPronostico.factoresPredisponentes = NULL, " +
           "f.seccionEtiopatogenicaPronostico.factoresDeterminantes = NULL, " +
           "f.seccionEtiopatogenicaPronostico.factoresDesencadenantes = NULL, " +
           "f.seccionEtiopatogenicaPronostico.pronosticoTipo = NULL, " +
           "f.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE f.id = :id")
    int limpiarSeccionEtiopatogenicaPronostico(@Param("id") Long id);
    
    // Método para verificar si una ficha tiene completas las nuevas secciones
    @Query("SELECT CASE WHEN (f.seccionAdolescencia IS NOT NULL AND " +
           "f.seccionFamiliar IS NOT NULL AND " +
           "f.seccionFuncionesPsicologicas IS NOT NULL AND " +
           "f.seccionRasgosExamenes IS NOT NULL AND " +
           "f.seccionEtiopatogenicaPronostico IS NOT NULL) THEN true ELSE false END " +
           "FROM FichaPsicologica f WHERE f.id = :id")
    Optional<Boolean> tieneCompletasNuevasSecciones(@Param("id") Long id);
    
    // Método para contar fichas por psicólogo con secciones completas
    @Query("SELECT COUNT(f) FROM FichaPsicologica f WHERE f.psicologo.id = :psicologoId AND " +
           "f.seccionAdolescencia IS NOT NULL AND " +
           "f.seccionFamiliar IS NOT NULL AND " +
           "f.seccionFuncionesPsicologicas IS NOT NULL AND " +
           "f.seccionRasgosExamenes IS NOT NULL AND " +
           "f.seccionEtiopatogenicaPronostico IS NOT NULL")
    Long contarFichasCompletasPorPsicologo(@Param("psicologoId") Long psicologoId);
    
    // Método para obtener fichas que tienen secciones específicas incompletas
    @Query("SELECT f FROM FichaPsicologica f WHERE " +
           "(:incluirAdolescencia = true AND f.seccionAdolescencia IS NULL) OR " +
           "(:incluirFamiliar = true AND f.seccionFamiliar IS NULL) OR " +
           "(:incluirFunciones = true AND f.seccionFuncionesPsicologicas IS NULL) OR " +
           "(:incluirRasgos = true AND f.seccionRasgosExamenes IS NULL) OR " +
           "(:incluirEtiopatogenica = true AND f.seccionEtiopatogenicaPronostico IS NULL)")
    List<FichaPsicologica> findFichasConSeccionesIncompletas(
            @Param("incluirAdolescencia") boolean incluirAdolescencia,
            @Param("incluirFamiliar") boolean incluirFamiliar,
            @Param("incluirFunciones") boolean incluirFunciones,
            @Param("incluirRasgos") boolean incluirRasgos,
            @Param("incluirEtiopatogenica") boolean incluirEtiopatogenica);
    
    // Devuelve id, cedula, apellidosNombres, unidadMilitar de todos los personal_militar relacionados a fichas
    @Query("SELECT pm.id, pm.cedula, pm.apellidosNombres, pm.unidadMilitar FROM FichaPsicologica f JOIN f.personalMilitar pm WHERE pm.unidadMilitar IS NOT NULL")
    List<Object[]> getPersonalMilitarUnidadMilitar();
}