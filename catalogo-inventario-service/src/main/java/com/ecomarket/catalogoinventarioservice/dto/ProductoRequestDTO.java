package com.ecomarket.catalogoinventarioservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    @NotBlank
    private String sku;
    
    @NotBlank
    private String nombre;
    
    private String descripcion;
    
    @NotNull
    @Positive
    private Double precioBase;
    
    @NotNull
    private Long categoriaId;
    
    @NotNull
    private Long estadoId;
    
    private String imagenUrl;
}
