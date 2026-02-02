package ec.mil.dsndft.servicio_gestion.service;

import ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10CreateRequestDTO;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10DTO;
import ec.mil.dsndft.servicio_gestion.model.dto.CatalogoDiagnosticoCie10UpdateRequestDTO;
import ec.mil.dsndft.servicio_gestion.repository.CatalogoDiagnosticoCie10Repository;
import ec.mil.dsndft.servicio_gestion.service.impl.CatalogoDiagnosticoCie10ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CatalogoDiagnosticoCie10ServiceImplTest {
    @Mock
    private CatalogoDiagnosticoCie10Repository repository;
    @InjectMocks
    private CatalogoDiagnosticoCie10ServiceImpl service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    @DisplayName("Debe crear diagnóstico correctamente")
    void crearDiagnostico_ok() {
        CatalogoDiagnosticoCie10CreateRequestDTO req = new CatalogoDiagnosticoCie10CreateRequestDTO();
        req.setCodigo("A00"); req.setNombre("Cólera"); req.setDescripcion("desc"); req.setNivel(1); req.setActivo(true);
        when(repository.findByCodigoIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            CatalogoDiagnosticoCie10 ent = inv.getArgument(0);
            ent.setId(1L); return ent;
        });
        CatalogoDiagnosticoCie10DTO dto = service.crear(req);
        assertThat(dto.getCodigo()).isEqualTo("A00");
        assertThat(dto.getNombre()).isEqualTo("Cólera");
        assertThat(dto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Debe lanzar error si código ya existe")
    void crearDiagnostico_codigoDuplicado() {
        CatalogoDiagnosticoCie10CreateRequestDTO req = new CatalogoDiagnosticoCie10CreateRequestDTO();
        req.setCodigo("A00"); req.setNombre("Cólera"); req.setDescripcion("desc"); req.setNivel(1); req.setActivo(true);
        when(repository.findByCodigoIgnoreCase(anyString())).thenReturn(Optional.of(new CatalogoDiagnosticoCie10()));
        assertThatThrownBy(() -> service.crear(req)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un diagnóstico CIE-10");
    }

    @Test
    @DisplayName("Debe obtener diagnóstico por ID si existe y está activo")
    void obtenerPorId_ok() {
        CatalogoDiagnosticoCie10 ent = CatalogoDiagnosticoCie10.builder().id(1L).codigo("A00").nombre("Cólera").activo(true).build();
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(ent));
        CatalogoDiagnosticoCie10DTO dto = service.obtenerPorId(1L);
        assertThat(dto.getCodigo()).isEqualTo("A00");
    }

    @Test
    @DisplayName("Debe lanzar error si diagnóstico no existe")
    void obtenerPorId_noExiste() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe actualizar diagnóstico correctamente")
    void actualizar_ok() {
        CatalogoDiagnosticoCie10 ent = CatalogoDiagnosticoCie10.builder().id(1L).codigo("A00").nombre("Cólera").activo(true).build();
        CatalogoDiagnosticoCie10UpdateRequestDTO req = new CatalogoDiagnosticoCie10UpdateRequestDTO();
        req.setCodigo("A01"); req.setNombre("Tifoidea"); req.setNivel(2);
        when(repository.findById(1L)).thenReturn(Optional.of(ent));
        when(repository.findByCodigoIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CatalogoDiagnosticoCie10DTO dto = service.actualizar(1L, req);
        assertThat(dto.getCodigo()).isEqualTo("A01");
        assertThat(dto.getNombre()).isEqualTo("Tifoidea");
    }

    @Test
    @DisplayName("Debe eliminar (desactivar) diagnóstico")
    void eliminar_ok() {
        CatalogoDiagnosticoCie10 ent = CatalogoDiagnosticoCie10.builder().id(1L).codigo("A00").nombre("Cólera").activo(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(ent));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service.eliminar(1L);
        assertThat(ent.getActivo()).isFalse();
    }
}
