package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.repository.FichaPsicologicaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@ConditionalOnBean(FichaPsicologicaRepository.class)
@ConditionalOnProperty(prefix = "app.debug.controllers", name = "enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "Debug unidad militar", description = "Consultas técnicas de apoyo sobre personal y unidad militar")
public class DebugUnidadMilitarController {
    private final FichaPsicologicaRepository fichaPsicologicaRepository;

    @GetMapping("/personal-unidad-militar")
    @Operation(summary = "Listar personal y unidad",
               description = "Devuelve combinaciones de personal militar y su unidad asociada para propósitos de debug")
    public List<Object[]> getPersonalUnidadMilitar() {
        // Devuelve id, cedula, apellidosNombres, unidadMilitar de todos los personal_militar relacionados a fichas
        return fichaPsicologicaRepository.getPersonalMilitarUnidadMilitar();
    }
}
