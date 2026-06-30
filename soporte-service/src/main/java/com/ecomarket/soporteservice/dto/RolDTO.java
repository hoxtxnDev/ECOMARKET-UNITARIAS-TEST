package com.ecomarket.soporteservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RolDTO {

    @JsonProperty("id")
    private Long idRol;
    private String nombre;

}
