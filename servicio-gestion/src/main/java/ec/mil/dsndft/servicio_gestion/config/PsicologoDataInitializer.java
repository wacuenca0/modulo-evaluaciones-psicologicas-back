package ec.mil.dsndft.servicio_gestion.config;

import ec.mil.dsndft.servicio_gestion.entity.CatalogUsuario;
import ec.mil.dsndft.servicio_gestion.entity.Psicologo;
import ec.mil.dsndft.servicio_gestion.repository.CatalogUsuarioRepository;
import ec.mil.dsndft.servicio_gestion.repository.PsicologoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PsicologoDataInitializer implements CommandLineRunner {

    private final PsicologoRepository psicologoRepository;
    private final CatalogUsuarioRepository catalogUsuarioRepository;

    @Value("${app.bootstrap.demo-psicologos.enabled:true}")
    private boolean demoPsicologosEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!demoPsicologosEnabled) {
            log.info("Carga de psicólogos demo deshabilitada");
            return;
        }

        // Para cada usuario inicial conocido en catálogo, aseguramos su psicólogo
        ensurePsicologoForUsuario("admin", "9999990001", "Administrador", "Sistema");
        ensurePsicologoForUsuario("psicologo01", "9999990002", "Psicólogo", "Por Defecto");
        ensurePsicologoForUsuario("observador01", "9999990003", "Observador", "Por Defecto");
    }

    private void ensurePsicologoForUsuario(String username, String cedula, String nombres, String apellidos) {
        try {
            CatalogUsuario user = catalogUsuarioRepository.findByUsernameIgnoreCase(username)
                .orElse(null);
            if (user == null || user.getId() == null) {
                log.warn("No se encontró usuario '{}' en servicio-catalogos. No se creará psicólogo.", username);
                return;
            }

            // Verificar si ya existe psicólogo enlazado a ese usuarioId
            if (psicologoRepository.findByUsuarioId(user.getId()).isPresent()) {
                log.info("Ya existe psicólogo para usuario '{}' (ID usuario: {}). Se omite creación.", username, user.getId());
                return;
            }

            Psicologo psicologo = Psicologo.builder()
                .cedula(cedula)
                .usuarioId(user.getId())
                .nombres(nombres)
                .apellidos(apellidos)
                .username(user.getUsername())
                .email(user.getEmail())
                .activo(true)
                .build();
            psicologo.refreshNombreCompleto();

            psicologoRepository.save(psicologo);
            log.info("Psicólogo creado/enlazado correctamente para usuario '{}' (ID usuario: {}).", username, user.getId());
        } catch (Exception ex) {
            log.error("Error al asegurar psicólogo para usuario '{}': {}", username, ex.getMessage());
        }
    }
}

