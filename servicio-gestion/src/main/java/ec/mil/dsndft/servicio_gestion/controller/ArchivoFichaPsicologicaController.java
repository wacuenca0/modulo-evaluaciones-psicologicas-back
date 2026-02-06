package ec.mil.dsndft.servicio_gestion.controller;

import ec.mil.dsndft.servicio_gestion.entity.ArchivoFichaPsicologica;
import ec.mil.dsndft.servicio_gestion.entity.FichaPsicologica;
import ec.mil.dsndft.servicio_gestion.repository.FichaPsicologicaRepository;
import ec.mil.dsndft.servicio_gestion.service.FtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/archivos-ficha")
@RequiredArgsConstructor
@ConditionalOnClass(name = "ec.mil.dsndft.servicio_gestion.service.FtpService")
@ConditionalOnProperty(prefix = "ficha.archivos.ftp", name = "enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "Archivos de ficha", description = "Subida de archivos asociados a fichas psicológicas")
public class ArchivoFichaPsicologicaController {
    private final FtpService ftpService;
    private final FichaPsicologicaRepository fichaPsicologicaRepository;

    @PostMapping("/{fichaId}/subir")
    @Operation(summary = "Subir archivos de una ficha",
               description = "Sube uno o varios archivos a un servidor FTP y los asocia a una ficha psicológica")
    public ResponseEntity<List<ArchivoFichaPsicologica>> subirArchivos(
            @PathVariable Long fichaId,
            @RequestParam("archivos") List<MultipartFile> archivos,
            @RequestParam String servidor,
            @RequestParam int puerto,
            @RequestParam String usuario,
            @RequestParam String clave,
            @RequestParam String rutaRemota
    ) throws Exception {
        FichaPsicologica ficha = fichaPsicologicaRepository.findById(fichaId)
                .orElseThrow(() -> new EntityNotFoundException("Ficha no encontrada"));
        List<ArchivoFichaPsicologica> resultado = new ArrayList<>();
        for (MultipartFile archivo : archivos) {
            boolean ok = ftpService.subirArchivo(servidor, puerto, usuario, clave, rutaRemota, archivo.getOriginalFilename(), archivo.getInputStream());
            if (ok) {
                ArchivoFichaPsicologica registro = new ArchivoFichaPsicologica();
                registro.setFichaPsicologica(ficha);
                registro.setNombreArchivo(archivo.getOriginalFilename());
                registro.setRutaFtp(rutaRemota + "/" + archivo.getOriginalFilename());
                registro.setFechaSubida(LocalDateTime.now());
                resultado.add(registro);
            }
        }
        return ResponseEntity.ok(resultado);
    }
}
