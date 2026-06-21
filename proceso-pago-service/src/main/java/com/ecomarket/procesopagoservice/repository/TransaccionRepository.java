package com.ecomarket.procesopagoservice.repository;

import com.ecomarket.procesopagoservice.model.TransaccionPago;
import com.ecomarket.procesopagoservice.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransaccionRepository extends JpaRepository<TransaccionPago, Long> {
    List<TransaccionPago> findByClienteId(Long clienteId);
    List<TransaccionPago> findByPedidoId(Long pedidoId);
    Optional<TransaccionPago> findByIdempotencyKey(String idempotencyKey);
    Optional<TransaccionPago> findFirstByPedidoIdAndEstadoNotIn(Long pedidoId, List<EstadoPago> estados);
    boolean existsByPedidoIdAndEstadoNombre(Long pedidoId, String nombreEstado);
}
