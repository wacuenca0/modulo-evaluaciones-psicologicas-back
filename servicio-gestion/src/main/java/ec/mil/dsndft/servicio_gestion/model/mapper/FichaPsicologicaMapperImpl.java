package ec.mil.dsndft.servicio_gestion.model.mapper;

import ec.mil.dsndft.servicio_gestion.model.dto.FichaDatosGeneralesRequestDTO;

import ec.mil.dsndft.servicio_gestion.entity.FichaPsicologica;
import ec.mil.dsndft.servicio_gestion.model.dto.FichaPsicologicaDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.FichaPsicologicaCompletaDTO;
import ec.mil.dsndft.servicio_gestion.model.value.TransferenciaInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class FichaPsicologicaMapperImpl implements FichaPsicologicaMapper {

    @Override
    public FichaPsicologicaCompletaDTO toCompletaDTO(FichaPsicologica entity) {
        if (entity == null) {
            return null;
        }
        FichaPsicologicaCompletaDTO dto = new FichaPsicologicaCompletaDTO();
        dto.setId(entity.getId());
        // Mapear datos generales
        FichaDatosGeneralesRequestDTO datosGenerales = new FichaDatosGeneralesRequestDTO();
        datosGenerales.setPersonalMilitarId(entity.getPersonalMilitar() != null ? entity.getPersonalMilitar().getId() : null);
        datosGenerales.setFechaEvaluacion(entity.getFechaEvaluacion());
        datosGenerales.setTipoEvaluacion(entity.getTipoEvaluacion());
        datosGenerales.setEstado(entity.getEstado() != null ? entity.getEstado().getCanonical() : null);
        dto.setDatosGenerales(datosGenerales);

        // Mapear secciones
        // Si el método espera FichaSeccionObservacionRequestDTO, se debe mapear correctamente
        // Aquí puedes adaptar el mapeo según el tipo esperado
        dto.setSeccionObservacion(null); // TODO: Implementar mapeo correcto si tienes el DTO adecuado
        dto.setSeccionPsicoanamnesis(null); // Si tienes un mapper específico, agrégalo aquí
        dto.setSeccionAdolescencia(null); // Si tienes un mapper específico, agrégalo aquí
        dto.setSeccionFamiliar(null); // Si tienes un mapper específico, agrégalo aquí
        dto.setSeccionFuncionesPsicologicas(null); // Si tienes un mapper específico, agrégalo aquí
        dto.setSeccionRasgosExamenes(null); // Si tienes un mapper específico, agrégalo aquí
        dto.setSeccionEtiopatogenicaPronostico(null); // Si tienes un mapper específico, agrégalo aquí
        dto.setSeccionDiagnosticoCondicion(null); // Si tienes un mapper específico, agrégalo aquí

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    public FichaPsicologicaDTO toDTO(FichaPsicologica entity) {
        if (entity == null) {
            return null;
        }

        FichaPsicologicaDTO dto = new FichaPsicologicaDTO();
        dto.setId(entity.getId());
        if (entity.getPersonalMilitar() != null) {
            dto.setPersonalMilitarId(entity.getPersonalMilitar().getId());
            dto.setPersonalMilitarNombre(entity.getPersonalMilitar().getApellidosNombres());
            dto.setPersonalMilitarCedula(entity.getPersonalMilitar().getCedula());
        }
        if (entity.getPsicologo() != null) {
            dto.setPsicologoId(entity.getPsicologo().getId());
            dto.setPsicologoNombre(entity.getPsicologo().getApellidosNombres());
            dto.setPsicologoUsername(entity.getPsicologo().getUsername());
        }
        if (entity.getCreadoPor() != null) {
            dto.setCreadoPorId(entity.getCreadoPor().getId());
            dto.setCreadoPorNombre(entity.getCreadoPor().getApellidosNombres());
            dto.setCreadoPorUsername(entity.getCreadoPor().getUsername());
        }
        if (entity.getActualizadoPor() != null) {
            dto.setActualizadoPorId(entity.getActualizadoPor().getId());
            dto.setActualizadoPorNombre(entity.getActualizadoPor().getApellidosNombres());
            dto.setActualizadoPorUsername(entity.getActualizadoPor().getUsername());
        }
        dto.setNumeroEvaluacion(entity.getNumeroEvaluacion());
        dto.setFechaEvaluacion(entity.getFechaEvaluacion());
        dto.setTipoEvaluacion(entity.getTipoEvaluacion());
        dto.setSeccionObservacion(mapObservacion(entity.getSeccionObservacion()));
        dto.setSeccionPrenatal(mapPrenatal(entity.getSeccionPrenatal()));
        dto.setSeccionNatal(mapNatal(entity.getSeccionNatal()));
        dto.setSeccionInfancia(mapInfancia(entity.getSeccionInfancia()));
        dto.setEstado(mapEstado(entity.getEstado()));
        dto.setCondicion(mapCondicion(entity.getCondicionClinica()));
        // Mapear lista de diagnósticos CIE-10 (siempre array, nunca null)
        if (entity.getDiagnosticosCie10() == null || entity.getDiagnosticosCie10().isEmpty()) {
            dto.setDiagnosticosCie10(new java.util.ArrayList<>());
        } else {
            dto.setDiagnosticosCie10(entity.getDiagnosticosCie10().stream().map(d -> {
                ec.mil.dsndft.servicio_gestion.model.dto.DiagnosticoCie10DTO diagDto = new ec.mil.dsndft.servicio_gestion.model.dto.DiagnosticoCie10DTO();
                diagDto.setId(d.getId());
                diagDto.setCodigo(d.getCodigo());
                diagDto.setNombre(d.getNombre());
                diagDto.setCategoriaPadre(d.getCategoriaPadre());
                diagDto.setNivel(d.getNivel());
                diagDto.setDescripcion(d.getDescripcion());
                return diagDto;
            }).collect(Collectors.toList()));
        }
        dto.setPlanFrecuencia(mapPlanFrecuencia(entity.getPlanSeguimiento()));
        dto.setPlanTipoSesion(mapPlanTipoSesion(entity.getPlanSeguimiento()));
        dto.setPlanDetalle(mapPlanDetalle(entity.getPlanSeguimiento()));
        dto.setUltimaFechaSeguimiento(entity.getUltimaFechaSeguimiento());
        dto.setProximoSeguimiento(entity.getProximoSeguimiento());
        TransferenciaInfo transferencia = entity.getTransferenciaInfo();
        if (transferencia != null) {
            dto.setTransferenciaFecha(transferencia.getFechaTransferencia());
            dto.setTransferenciaUnidad(transferencia.getUnidadDestino());
            dto.setTransferenciaObservacion(transferencia.getObservacion());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    public List<FichaPsicologicaDTO> toDTOs(List<FichaPsicologica> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
            .filter(Objects::nonNull)
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
