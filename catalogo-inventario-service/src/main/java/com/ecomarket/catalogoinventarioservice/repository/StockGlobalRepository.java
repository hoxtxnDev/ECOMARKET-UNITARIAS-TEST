package com.ecomarket.catalogoinventarioservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecomarket.catalogoinventarioservice.model.StockGlobal;

public interface StockGlobalRepository extends JpaRepository<StockGlobal, Long> {
    Optional<StockGlobal> findByProductoId(Long productoId);
}
