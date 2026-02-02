package ec.mil.dsndft.servicio_gestion.repository;

import ec.mil.dsndft.servicio_gestion.entity.AtencionPsicologica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AtencionPsicologicaRepository extends JpaRepository<AtencionPsicologica, Long> {
    
    Page<AtencionPsicologica> findByPsicologoId(Long psicologoId, Pageable pageable);

    Page<AtencionPsicologica> findByPersonalMilitarId(Long personalMilitarId, Pageable pageable);

    List<AtencionPsicologica> findByFechaAtencion(LocalDate fechaAtencion);

    List<AtencionPsicologica> findByPsicologoIdAndFechaAtencion(Long psicologoId, LocalDate fechaAtencion);

    Page<AtencionPsicologica> findByEstado(String estado, Pageable pageable);
    @Query("""
        SELECT a FROM AtencionPsicologica a 
        WHERE (:estadoAtencion IS NULL OR a.estado = :estadoAtencion)
        AND (:nombre IS NULL OR LOWER(a.personalMilitar.apellidosNombres) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:fecha IS NULL OR a.fechaAtencion = :fecha)
    """)
    Page<AtencionPsicologica> findByFiltroAtencion(
        @Param("estadoAtencion") String estadoAtencion,
        @Param("nombre") String nombre,
        @Param("fecha") java.time.LocalDate fecha,
        Pageable pageable
    );
    
    @Query("SELECT a FROM AtencionPsicologica a WHERE a.fechaAtencion = CURRENT_DATE AND a.estado = 'PROGRAMADA'")
    List<AtencionPsicologica> findAtencionesProgramadasHoy();
}