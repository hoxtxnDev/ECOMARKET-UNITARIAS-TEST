package com.ecomarket.pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "item_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long pedidoId;

    @NotNull
    private Long productoId;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @PositiveOrZero
    private Double precioUnitarioHistorico;
}
