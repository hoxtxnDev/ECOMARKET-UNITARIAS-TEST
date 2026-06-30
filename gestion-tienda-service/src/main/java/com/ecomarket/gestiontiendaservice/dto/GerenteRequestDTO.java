package com.ecomarket.gestiontiendaservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GerenteRequestDTO {

    @NotNull
    private Long gerenteCargoId;
}
