package com.ecomarket.catalogoinventarioservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CantidadGlobalDTO {

    private Long productoId;

    @Positive
    private Integer cantidad;
}
