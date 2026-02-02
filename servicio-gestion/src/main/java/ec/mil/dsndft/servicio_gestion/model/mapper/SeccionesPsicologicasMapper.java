package ec.mil.dsndft.servicio_gestion.model.mapper;

import ec.mil.dsndft.servicio_gestion.model.dto.AdolescenciaJuventudAdultezDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.PsicoanamnesisFamiliarDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.ExamenFuncionesPsicologicasDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.RasgosPersonalidadExamenesDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.FormulacionEtiopatogenicaPronosticoDTO;
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisAdolescenciaJuventudAdultez;
import ec.mil.dsndft.servicio_gestion.model.value.PsicoanamnesisFamiliar;
import ec.mil.dsndft.servicio_gestion.model.value.ExamenFuncionesPsicologicas;
import ec.mil.dsndft.servicio_gestion.model.value.DiagnosticoRasgosExamenesPsicologicos;
import ec.mil.dsndft.servicio_gestion.model.value.FormulacionEtiopatogenicaPronostico;

public class SeccionesPsicologicasMapper {
    public AdolescenciaJuventudAdultezDTO toAdolescenciaJuventudAdultezDTO(PsicoanamnesisAdolescenciaJuventudAdultez value) {
        if (value == null) return null;
        AdolescenciaJuventudAdultezDTO dto = new AdolescenciaJuventudAdultezDTO();
        dto.setHabilidadesSociales(value.getHabilidadesSociales());
        dto.setTrastorno(value.getTrastorno());
        dto.setHistoriaPersonal(value.getHistoriaPersonal());
        dto.setMaltratoAdultoProblemasNegligencia(value.getMaltratoAdultoProblemasNegligencia());
        dto.setProblemasRelacionadosCircunstanciasLegales(value.getProblemasRelacionadosCircunstanciasLegales());
        dto.setTratamientosPsicologicosPsiquiatricos(value.getTratamientosPsicologicosPsiquiatricos());
        dto.setObservacion(value.getObservacion());
        return dto;
    }

    public PsicoanamnesisFamiliarDTO toPsicoanamnesisFamiliarDTO(PsicoanamnesisFamiliar value) {
        if (value == null) return null;
        PsicoanamnesisFamiliarDTO dto = new PsicoanamnesisFamiliarDTO();
        dto.setMiembrosConQuienesConvive(value.getMiembrosConQuienesConvive());
        dto.setAntecedentesPatologicosFamiliares(value.getAntecedentesPatologicosFamiliares());
        dto.setTieneAlgunaEnfermedad(value.getTieneAlgunaEnfermedad());
        dto.setTipoEnfermedad(value.getTipoEnfermedad());
        dto.setObservacion(value.getObservacion());
        return dto;
    }

    public ExamenFuncionesPsicologicasDTO toExamenFuncionesPsicologicasDTO(ExamenFuncionesPsicologicas value) {
        if (value == null) return null;
        ExamenFuncionesPsicologicasDTO dto = new ExamenFuncionesPsicologicasDTO();
        dto.setOrientacion(value.getOrientacion());
        dto.setAtencion(value.getAtencion());
        dto.setSensopercepciones(value.getSensopercepciones());
        dto.setVoluntad(value.getVoluntad());
        dto.setJuicioRazonamiento(value.getJuicioRazonamiento());
        dto.setNutricion(value.getNutricion());
        dto.setSueno(value.getSueno());
        dto.setSexual(value.getSexual());
        dto.setPensamientoCurso(value.getPensamientoCurso());
        dto.setPensamientoEstructura(value.getPensamientoEstructura());
        dto.setPensamientoContenido(value.getPensamientoContenido());
        dto.setConcienciaEnfermedadTratamiento(value.getConcienciaEnfermedadTratamiento());
        return dto;
    }

    public RasgosPersonalidadExamenesDTO toRasgosPersonalidadExamenesDTO(DiagnosticoRasgosExamenesPsicologicos value) {
        if (value == null) return null;
        RasgosPersonalidadExamenesDTO dto = new RasgosPersonalidadExamenesDTO();
        dto.setRasgo(value.getRasgo());
        dto.setObservacion(value.getObservacion());
        dto.setExamenesPsicologicos(value.getExamenesPsicologicos());
        return dto;
    }

    public FormulacionEtiopatogenicaPronosticoDTO toFormulacionEtiopatogenicaPronosticoDTO(FormulacionEtiopatogenicaPronostico value) {
        if (value == null) return null;
        FormulacionEtiopatogenicaPronosticoDTO dto = new FormulacionEtiopatogenicaPronosticoDTO();
        dto.setFactoresPredisponentes(value.getFactoresPredisponentes());
        dto.setFactoresDeterminantes(value.getFactoresDeterminantes());
        dto.setFactoresDesencadenantes(value.getFactoresDesencadenantes());
        dto.setPronosticoTipo(value.getPronosticoTipo());
        return dto;
    }
}
