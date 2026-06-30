package com.ecomarket.soporteservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoporteTicketRequestDTO {

    @NotNull(message = "El ID de la categoria es obligatorio.")
    private Long categoriaId;

    @NotBlank(message = "El asunto es obligatorio.")
    private String asunto;

    private Long pedidoId;
    
}
