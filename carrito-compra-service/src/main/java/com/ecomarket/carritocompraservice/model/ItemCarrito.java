package com.ecomarket.carritocompraservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_carrito")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Carrito carrito;

    @NotNull
    @Column(nullable = false)
    private Long productoId;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double precioUnitarioAgregado;

    @NotNull
    @Column(nullable = false)
    private Integer posicion;

    public Double calcularSubtotalItem() {
        return BigDecimal.valueOf(this.cantidad)
                .multiply(BigDecimal.valueOf(this.precioUnitarioAgregado))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}
