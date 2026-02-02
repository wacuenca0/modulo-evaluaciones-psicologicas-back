package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.repository.FichaPsicologicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/reportes-debug")
@RequiredArgsConstructor
public class ReporteDebugController {
    private final FichaPsicologicaRepository fichaPsicologicaRepository;

    @GetMapping("/personal-unidad-militar")
    public ResponseEntity<List<Object[]>> getPersonalUnidadMilitar() {
        List<Object[]> data = fichaPsicologicaRepository.getPersonalMilitarUnidadMilitar();
        return ResponseEntity.ok(data);
    }
}
