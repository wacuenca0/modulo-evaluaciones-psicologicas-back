

package ec.mil.dsndft.servicio_gestion.model.mapper;


import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.AtencionPsicologicaResponseDTO;
import ec.mil.dsndft.servicio_gestion.entity.AtencionPsicologica;
import ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AtencionPsicologicaMapper {
    AtencionPsicologica toEntity(AtencionPsicologicaRequestDTO dto);
    AtencionPsicologicaResponseDTO toResponseDTO(AtencionPsicologica entity);
    List<AtencionPsicologicaResponseDTO> toResponseDTOList(List<AtencionPsicologica> entities);

    // Diagnóstico mapping (manual, as needed)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigo", source = "codigo")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "descripcion", source = "descripcion")
    AtencionPsicologicaResponseDTO.DiagnosticoCie10DTO diagnosticoToDto(ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10 entity);
    List<AtencionPsicologicaResponseDTO.DiagnosticoCie10DTO> diagnosticosToDto(List<ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10> entities);
}
