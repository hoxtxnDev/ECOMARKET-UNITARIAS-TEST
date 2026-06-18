package com.horacio.ecomarket.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InhabilitarCredencialesRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
}
