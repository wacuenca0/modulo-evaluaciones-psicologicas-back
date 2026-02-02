package ec.mil.dsndft.servicio_gestion.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalMilitarDTO {
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Long id;
    @com.fasterxml.jackson.annotation.JsonProperty("cedula")
    private String cedula;
    @com.fasterxml.jackson.annotation.JsonProperty("apellidosNombres")
    private String apellidosNombres;
    @com.fasterxml.jackson.annotation.JsonProperty("tipoPersona")
    private String tipoPersona;
    @com.fasterxml.jackson.annotation.JsonProperty("esMilitar")
    private Boolean esMilitar;
    @com.fasterxml.jackson.annotation.JsonProperty("fechaNacimiento")
    private LocalDate fechaNacimiento;
    @com.fasterxml.jackson.annotation.JsonProperty("edad")
    private Integer edad;
    @com.fasterxml.jackson.annotation.JsonProperty("sexo")
    private String sexo;
    @com.fasterxml.jackson.annotation.JsonProperty("etnia")
    private String etnia;
    @com.fasterxml.jackson.annotation.JsonProperty("estadoCivil")
    private String estadoCivil;
    @com.fasterxml.jackson.annotation.JsonProperty("nroHijos")
    private Integer nroHijos;
    @com.fasterxml.jackson.annotation.JsonProperty("ocupacion")
    private String ocupacion;
    @com.fasterxml.jackson.annotation.JsonProperty("servicioActivo")
    private Boolean servicioActivo;
    @com.fasterxml.jackson.annotation.JsonProperty("servicioPasivo")
    private Boolean servicioPasivo;
    @com.fasterxml.jackson.annotation.JsonProperty("seguro")
    private String seguro;
    @com.fasterxml.jackson.annotation.JsonProperty("grado")
    private String grado;
    @com.fasterxml.jackson.annotation.JsonProperty("especialidad")
    private String especialidad;
    @com.fasterxml.jackson.annotation.JsonProperty("unidadMilitar")
    private String unidadMilitar;
    @com.fasterxml.jackson.annotation.JsonProperty("provincia")
    private String provincia;
    @com.fasterxml.jackson.annotation.JsonProperty("canton")
    private String canton;
    @com.fasterxml.jackson.annotation.JsonProperty("parroquia")
    private String parroquia;
    @com.fasterxml.jackson.annotation.JsonProperty("barrioSector")
    private String barrioSector;
    @com.fasterxml.jackson.annotation.JsonProperty("telefono")
    private String telefono;
    @com.fasterxml.jackson.annotation.JsonProperty("celular")
    private String celular;
    @com.fasterxml.jackson.annotation.JsonProperty("email")
    private String email;
    @com.fasterxml.jackson.annotation.JsonProperty("activo")
    private Boolean activo;

    public PersonalMilitarDTO() {
    }
}