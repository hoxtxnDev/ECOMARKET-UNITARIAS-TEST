package com.ecomarket.procesopagoservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupon_descuento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CuponDescuento {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String codigo;

    @NotNull
    @PositiveOrZero
    private BigDecimal porcentajeDescuento;

    @PositiveOrZero
    private BigDecimal montoMaximoDescuento;

    @NotNull
    private LocalDateTime fechaExpiracion;

    @NotNull
    private Boolean activo;

    public boolean esValido() {
        return Boolean.TRUE.equals(activo) && LocalDateTime.now().isBefore(fechaExpiracion);
    }
}