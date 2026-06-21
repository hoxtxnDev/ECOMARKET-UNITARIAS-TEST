package com.ecomarket.gestiontiendaservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    private String telefono;

    private Long gerenteCargoId;
}
