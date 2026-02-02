package ec.mil.dsndft.servicio_catalogos.client;

import ec.mil.dsndft.servicio_catalogos.model.integration.PsicologoCreateRequest;
import ec.mil.dsndft.servicio_catalogos.model.integration.PsicologoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "psicologo-service", url = "${psicologo.service.url:http://127.0.0.1:8082/gestion/api}")
public interface PsicologoFeignClient {
    @PostMapping("/psicologos")
    PsicologoResponse crearPsicologo(@RequestBody PsicologoCreateRequest request);

    @GetMapping("/psicologos/buscar")
    PsicologoResponse buscarPorCedula(@RequestParam("cedula") String cedula);
}
