package ec.mil.dsndft.servicio_gestion.repository;

import ec.mil.dsndft.servicio_gestion.entity.AtencionPsicologicaHistorial;
import ec.mil.dsndft.servicio_gestion.entity.AtencionPsicologica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AtencionPsicologicaHistorialRepository extends JpaRepository<AtencionPsicologicaHistorial, Long> {
    List<AtencionPsicologicaHistorial> findByAtencionIdOrderByFechaCambioAsc(Long atencionId);
}
