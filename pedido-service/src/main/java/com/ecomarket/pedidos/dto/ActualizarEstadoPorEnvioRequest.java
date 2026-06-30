package com.ecomarket.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoPorEnvioRequest {
    @NotNull
    private Long pedidoId;
    @NotBlank
    private String estadoEnvioNombre;
}
