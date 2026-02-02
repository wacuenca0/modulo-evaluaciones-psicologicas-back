package ec.mil.dsndft.servicio_gestion.service;

import ec.mil.dsndft.servicio_gestion.model.dto.*;

import java.util.List;

public interface FichaPsicologicaService {
    
    // Métodos existentes
    void eliminarFicha(Long id);

    List<FichaPsicologicaDTO> listar(Long psicologoId, Long personalMilitarId, String estado, String condicion, Boolean soloActivas);

    List<FichaPsicologicaDTO> listarPorCondicion(String condicion, Long psicologoId, Long personalMilitarId);

    FichaPsicologicaDTO obtenerPorId(Long id);

    FichaPsicologicaDTO obtenerPorNumeroEvaluacion(String numeroEvaluacion);

    FichaPsicologicaDTO crearFicha(FichaDatosGeneralesRequestDTO request);

    FichaPsicologicaDTO actualizarDatosGenerales(Long id, FichaDatosGeneralesRequestDTO request);

    FichaPsicologicaDTO guardarSeccionObservacion(Long id, FichaSeccionObservacionRequestDTO request);

    FichaPsicologicaDTO guardarSeccionPsicoanamnesis(Long id, FichaSeccionPsicoanamnesisRequestDTO request);

    FichaPsicologicaDTO actualizarCondicion(Long id, FichaCondicionRequestDTO request);

    FichaPsicologicaDTO actualizarEstado(Long id, String estado);

    String generarNumeroEvaluacionPreview();

    FichaPsicologicaDTO eliminarSeccionObservacion(Long id);

    FichaPsicologicaDTO eliminarSeccionPsicoanamnesis(Long id);

    FichaPsicologicaDTO limpiarCondicionClinica(Long id);

    FichaPsicologicaDTO finalizarFicha(Long id);

    List<FichaPsicologicaDTO> obtenerHistorialPorPersonal(Long personalMilitarId);
    
    // NUEVOS MÉTODOS para las nuevas secciones
    
    // Sección 6: Adolescencia, Juventud y Adultez
    FichaPsicologicaDTO guardarSeccionAdolescencia(Long id, AdolescenciaJuventudAdultezDTO request);
    
    FichaPsicologicaDTO actualizarSeccionAdolescencia(Long id, AdolescenciaJuventudAdultezDTO request);
    
    FichaPsicologicaDTO eliminarSeccionAdolescencia(Long id);
    
    // Sección 7: Psicoanamnesis Familiar
    FichaPsicologicaDTO guardarSeccionFamiliar(Long id, PsicoanamnesisFamiliarDTO request);
    
    FichaPsicologicaDTO actualizarSeccionFamiliar(Long id, PsicoanamnesisFamiliarDTO request);
    
    FichaPsicologicaDTO eliminarSeccionFamiliar(Long id);
    
    // Sección 8: Exámenes de Funciones Psicológicas
    FichaPsicologicaDTO guardarSeccionFuncionesPsicologicas(Long id, ExamenFuncionesPsicologicasDTO request);
    
    FichaPsicologicaDTO actualizarSeccionFuncionesPsicologicas(Long id, ExamenFuncionesPsicologicasDTO request);
    
    FichaPsicologicaDTO eliminarSeccionFuncionesPsicologicas(Long id);
    
    // Secciones 10 y 11: Rasgos de Personalidad y Exámenes Psicológicos
    FichaPsicologicaDTO guardarSeccionRasgosExamenes(Long id, RasgosPersonalidadExamenesDTO request);
    
    FichaPsicologicaDTO actualizarSeccionRasgosExamenes(Long id, RasgosPersonalidadExamenesDTO request);
    
    FichaPsicologicaDTO eliminarSeccionRasgosExamenes(Long id);
    
    // Secciones 12 y 13: Formulación Etiopatogénica y Pronóstico
    FichaPsicologicaDTO guardarSeccionEtiopatogenicaPronostico(Long id, FormulacionEtiopatogenicaPronosticoDTO request);
    
    FichaPsicologicaDTO actualizarSeccionEtiopatogenicaPronostico(Long id, FormulacionEtiopatogenicaPronosticoDTO request);
    
    FichaPsicologicaDTO eliminarSeccionEtiopatogenicaPronostico(Long id);
    
    // Método para actualizar todas las secciones nuevas de una vez
    FichaPsicologicaDTO actualizarTodasSeccionesNuevas(Long id, FichaSeccionesNuevasRequestDTO request);
    
    // Método para obtener una ficha con todas las secciones completas
    FichaPsicologicaCompletaDTO obtenerFichaCompletaPorId(Long id);
    
    // Método para verificar si una ficha tiene completas las nuevas secciones
    boolean verificarSeccionesNuevasCompletas(Long id);
    
    // Método para crear ficha con todas las secciones iniciales
    FichaPsicologicaCompletaDTO crearFichaCompleta(FichaPsicologicaCompletaRequestDTO request);
}