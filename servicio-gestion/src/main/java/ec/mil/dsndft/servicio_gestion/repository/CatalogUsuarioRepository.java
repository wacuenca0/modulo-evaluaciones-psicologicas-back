package ec.mil.dsndft.servicio_gestion.repository;

import ec.mil.dsndft.servicio_gestion.entity.CatalogUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatalogUsuarioRepository extends JpaRepository<CatalogUsuario, Long> {

    Optional<CatalogUsuario> findByUsernameIgnoreCase(String username);
}
