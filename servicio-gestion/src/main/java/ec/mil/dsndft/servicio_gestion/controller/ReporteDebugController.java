package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.repository.FichaPsicologicaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/reportes-debug")
@RequiredArgsConstructor
@Tag(name = "Reportes debug", description = "Consultas de soporte para diagnóstico y pruebas")
public class ReporteDebugController {
    private final FichaPsicologicaRepository fichaPsicologicaRepository;

    @GetMapping("/personal-unidad-militar")
    @Operation(summary = "Reporte debug de personal/unidad",
               description = "Obtiene datos crudos de personal militar y unidad militar para análisis técnico")
    public ResponseEntity<List<Object[]>> getPersonalUnidadMilitar() {
        List<Object[]> data = fichaPsicologicaRepository.getPersonalMilitarUnidadMilitar();
        return ResponseEntity.ok(data);
    }
}
