package com.horacio.ecomarket.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearDireccionRequest {

    @NotBlank
    private String calle;

    private String numero;

    private String departamento;

    @NotBlank
    private String ciudad;

    @NotBlank
    private String region;

    private String codigoPostal;

    @NotBlank
    private String destinatario;

    private Boolean esPredeterminada = false;
}