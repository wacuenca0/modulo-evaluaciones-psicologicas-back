package ec.mil.dsndft.servicio_gestion.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ExamenFuncionesPsicologicas {
    @Column(name = "examen_orientacion", columnDefinition = "CLOB")
    private String orientacion;
    @Column(name = "examen_atencion", columnDefinition = "CLOB")
    private String atencion;
    @Column(name = "examen_sensopercepciones", columnDefinition = "CLOB")
    private String sensopercepciones;
    @Column(name = "examen_voluntad", columnDefinition = "CLOB")
    private String voluntad;
    @Column(name = "examen_juicio_razonamiento", columnDefinition = "CLOB")
    private String juicioRazonamiento;
    @Column(name = "examen_nutricion", columnDefinition = "CLOB")
    private String nutricion;
    @Column(name = "examen_sueno", columnDefinition = "CLOB")
    private String sueno;
    @Column(name = "examen_sexual", columnDefinition = "CLOB")
    private String sexual;
    @Column(name = "examen_pensamiento_curso", columnDefinition = "CLOB")
    private String pensamientoCurso;
    @Column(name = "examen_pensamiento_estructura", columnDefinition = "CLOB")
    private String pensamientoEstructura;
    @Column(name = "examen_pensamiento_contenido", columnDefinition = "CLOB")
    private String pensamientoContenido;
    @Column(name = "examen_conciencia_enfermedad", columnDefinition = "CLOB")
    private String concienciaEnfermedadTratamiento;
}