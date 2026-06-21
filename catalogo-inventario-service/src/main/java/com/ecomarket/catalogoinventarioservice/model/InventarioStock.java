package com.ecomarket.catalogoinventarioservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventario_stock")
public class InventarioStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long productoId;

    @NotNull
    @Column(nullable = false)
    private Long sucursalId;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer cantidadDisponible;

    @PositiveOrZero
    private Integer cantidadReservada = 0;

    @PositiveOrZero
    private Integer stockMinimoAlerta;

    private LocalDateTime ultimaReposicion;

    public boolean hayStock(Integer cantidadSolicitada) {
        return this.cantidadDisponible >= cantidadSolicitada;
    }
}
