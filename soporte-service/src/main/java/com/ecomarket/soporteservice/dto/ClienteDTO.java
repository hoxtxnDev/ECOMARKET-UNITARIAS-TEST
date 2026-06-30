package com.ecomarket.soporteservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClienteDTO {

    @JsonProperty("id")
    private Long clienteId;
    private String correo;
    private String nombre;
    @JsonProperty("rol")
    private RolDTO rolUsuario;

}
