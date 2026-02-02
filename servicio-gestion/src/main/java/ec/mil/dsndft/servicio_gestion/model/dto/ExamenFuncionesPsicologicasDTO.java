package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExamenFuncionesPsicologicasDTO {
    @Size(max = 4000, message = "La orientación no debe superar 4000 caracteres")
    @JsonAlias({"orientacion"})
    private String orientacion;
    @Size(max = 4000, message = "La atención no debe superar 4000 caracteres")
    @JsonAlias({"atencion"})
    private String atencion;
    @Size(max = 4000, message = "Las sensopercepciones no deben superar 4000 caracteres")
    @JsonAlias({"sensopercepciones"})
    private String sensopercepciones;
    @Size(max = 4000, message = "La voluntad no debe superar 4000 caracteres")
    @JsonAlias({"voluntad"})
    private String voluntad;
    @Size(max = 4000, message = "El juicio y razonamiento no debe superar 4000 caracteres")
    @JsonAlias({"juicio_razonamiento"})
    private String juicioRazonamiento;
    @Size(max = 4000, message = "La nutrición no debe superar 4000 caracteres")
    @JsonAlias({"nutricion"})
    private String nutricion;
    @Size(max = 4000, message = "El sueño no debe superar 4000 caracteres")
    @JsonAlias({"sueno"})
    private String sueno;
    @Size(max = 4000, message = "La sexualidad no debe superar 4000 caracteres")
    @JsonAlias({"sexual"})
    private String sexual;
    @Size(max = 4000, message = "El curso del pensamiento no debe superar 4000 caracteres")
    @JsonAlias({"pensamiento_curso"})
    private String pensamientoCurso;
    @Size(max = 4000, message = "La estructura del pensamiento no debe superar 4000 caracteres")
    @JsonAlias({"pensamiento_estructura"})
    private String pensamientoEstructura;
    @Size(max = 4000, message = "El contenido del pensamiento no debe superar 4000 caracteres")
    @JsonAlias({"pensamiento_contenido"})
    private String pensamientoContenido;
    @Size(max = 4000, message = "La conciencia de enfermedad no debe superar 4000 caracteres")
    @JsonAlias({"conciencia_enfermedad_tratamiento"})
    private String concienciaEnfermedadTratamiento;
}
