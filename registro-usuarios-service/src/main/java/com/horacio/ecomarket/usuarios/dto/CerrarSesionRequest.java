package com.horacio.ecomarket.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CerrarSesionRequest {

    @NotBlank(message = "El token es obligatorio")
    private String token;
}
