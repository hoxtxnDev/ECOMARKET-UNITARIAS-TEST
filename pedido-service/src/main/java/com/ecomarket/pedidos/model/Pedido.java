package com.ecomarket.pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long clienteId;

    private Long carritoId;

    @NotNull
    @PositiveOrZero
    private Double subtotal;

    @NotNull
    @PositiveOrZero
    private Double total;

    private Long direccionEnvioId;

    private Long metodoPagoId;

    @ManyToOne
    @JoinColumn(name = "estado_pedido_id")
    private EstadoPedido estado;

    private LocalDateTime fechaCreacion;
}
