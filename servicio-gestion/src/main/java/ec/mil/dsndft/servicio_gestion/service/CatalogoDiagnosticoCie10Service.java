package ec.mil.dsndft.servicio_gestion.service;

import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10CreateRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10DTO;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10UpdateRequestDTO;
import java.util.List;

public interface CatalogoDiagnosticoCie10Service {

    org.springframework.data.domain.Page<CatalogoDiagnosticoCie10DTO> listarActivos(String termino, org.springframework.data.domain.Pageable pageable);

    CatalogoDiagnosticoCie10DTO obtenerPorId(Long id);

    CatalogoDiagnosticoCie10DTO crear(CatalogoDiagnosticoCie10CreateRequestDTO request);

    CatalogoDiagnosticoCie10DTO actualizar(Long id, CatalogoDiagnosticoCie10UpdateRequestDTO request);

    void eliminar(Long id);
}
