package com.ecomarket.procesopagoservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion_pago")
@Data @NoArgsConstructor @AllArgsConstructor
public class TransaccionPago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long clienteId;

    @NotNull
    @PositiveOrZero
    private Double montoSubtotal;

    @PositiveOrZero
    private Double montoDescuento;

    @NotNull
    @PositiveOrZero
    private Double montoTotal;

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPagoTransaccion metodoPago;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoPago estado;

    private Long cuponUtilizadoId;
    private String tokenTransbank;
    private String codigoAutorizacion;
    
    @Column(unique = true)
    private String idempotencyKey;
    
    // Trazabilidad de Auditoría

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaUltimaActualizacion;
    private String mensajeError;
}

