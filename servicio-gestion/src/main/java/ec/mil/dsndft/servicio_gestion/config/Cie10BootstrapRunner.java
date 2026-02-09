package ec.mil.dsndft.servicio_gestion.config;

import ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.repository.CatalogoDiagnosticoCie10Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.bootstrap.cie10", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Cie10BootstrapRunner implements ApplicationRunner {

    private final CatalogoDiagnosticoCie10Repository cie10Repository;

    @Override
    public void run(ApplicationArguments args) {
        // Semillas mínimas para que el flujo clínico funcione “out of the box” en entornos nuevos.
        // Seguro en producción: solo inserta códigos faltantes (no toca los existentes).
        List<CatalogoDiagnosticoCie10> seeds = List.of(
            CatalogoDiagnosticoCie10.builder()
                .codigo("F41.1")
                .nombre("Ansiedad generalizada")
                .descripcion("Trastorno de ansiedad generalizada")
                .nivel(3)
                .activo(Boolean.TRUE)
                .build(),
            CatalogoDiagnosticoCie10.builder()
                .codigo("F32.0")
                .nombre("Episodio depresivo leve")
                .descripcion("Episodio depresivo leve")
                .nivel(3)
                .activo(Boolean.TRUE)
                .build(),
            CatalogoDiagnosticoCie10.builder()
                .codigo("F43.2")
                .nombre("Trastorno de adaptación")
                .descripcion("Reacción a estrés grave y trastornos de adaptación")
                .nivel(3)
                .activo(Boolean.TRUE)
                .build()
        );

        int inserted = 0;
        for (CatalogoDiagnosticoCie10 seed : seeds) {
            String codigo = seed.getCodigo();
            if (codigo == null || codigo.isBlank()) {
                continue;
            }
            if (cie10Repository.findByCodigoIgnoreCase(codigo.trim()).isPresent()) {
                continue;
            }
            cie10Repository.save(seed);
            inserted++;
        }

        if (inserted > 0) {
            log.info("CIE-10 bootstrap: insertados {} diagnósticos mínimos.", inserted);
        }
    }
}
