package com.ecomarket.carritocompraservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long clienteId;

    @NotNull
    @PositiveOrZero
    private Double subtotal = 0.0;

    private Long metodoEnvioId;

    private Long metodoPagoId;

    private LocalDateTime fechaUltimaModificacion;

    @Column(nullable = false)
    private Boolean activo = false;

    @Column(nullable = false)
    private Boolean cerrado = false;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicion ASC")
    private List<ItemCarrito> items = new ArrayList<>();

    public Double calcularTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                .map(ItemCarrito::calcularSubtotalItem)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}
