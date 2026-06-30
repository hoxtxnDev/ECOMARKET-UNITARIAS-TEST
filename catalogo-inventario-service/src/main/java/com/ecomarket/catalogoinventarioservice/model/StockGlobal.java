package com.ecomarket.catalogoinventarioservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "stock_global")
public class StockGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private Long productoId;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer cantidadDisponible = 0;

    private LocalDateTime ultimaActualizacion;

    @PrePersist
    public void prePersist() {
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void incrementarStock(Integer cantidad) {
        this.cantidadDisponible += cantidad;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void disminuirStock(Integer cantidad) {
        this.cantidadDisponible -= cantidad;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public boolean hayStock(Integer cantidad) {
        return this.cantidadDisponible >= cantidad;
    }
}
