package com.ecomarket.catalogoinventarioservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CantidadDTO {

    private Long productoId;

    private Long sucursalId;

    @Positive
    private Integer cantidad;
}
