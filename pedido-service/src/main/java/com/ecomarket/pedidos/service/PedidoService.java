package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.dto.CarritoDTO;
import com.ecomarket.pedidos.model.*;
import com.ecomarket.pedidos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public Pedido generarPedidoDesdeCarrito(Long clienteId, Long carritoId) {
        // 1. Obtener datos del carrito desde carritocompraservice
        String cartUrl = "http://localhost:8082/api/carrito/" + carritoId;
        CarritoDTO carrito = restTemplate.getForObject(cartUrl, CarritoDTO.class);

        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío o no existe.");
        }

        // 2. Crear el pedido
        EstadoPedido estadoInicial = estadoPedidoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no encontrado"));

        Pedido pedido = Pedido.builder()
                .clienteId(clienteId)
                .subtotal(carrito.getSubtotal())
                .total(carrito.getSubtotal()) // Simplificado: sin impuestos/envío por ahora
                .estado(estadoInicial)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 3. Crear items del pedido
        List<ItemPedido> items = carrito.getItems().stream().map(itemDto -> 
            ItemPedido.builder()
                .pedido(pedidoGuardado)
                .productoId(itemDto.getProductoId())
                .cantidad(itemDto.getCantidad())
                .precioUnitarioHistorico(itemDto.getPrecioUnitarioAgregado())
                .build()
        ).collect(Collectors.toList());

        itemPedidoRepository.saveAll(items);

        // 4. Vaciar el carrito en carritocompraservice
        try {
            String emptyCartUrl = "http://localhost:8082/api/carrito/" + clienteId + "/vaciar";
            restTemplate.delete(emptyCartUrl);
        } catch (Exception e) {
            log.warn("No se pudo vaciar el carrito {}", carritoId, e);
        }

        registrarLog(clienteId, "PEDIDO_GENERADO", "Pedido generado exitosamente con ID: " + pedidoGuardado.getId() + ". Total: " + pedidoGuardado.getTotal());

        log.info("Pedido {} generado para cliente {} desde carrito {}", pedidoGuardado.getId(), clienteId, carritoId);

        return pedidoGuardado;
    }

    @Transactional
    public Pedido actualizarEstado(Long pedidoId, Long nuevoEstadoId) {
        Pedido pedido = buscarPorId(pedidoId);
        EstadoPedido estado = estadoPedidoRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerHistorialCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido buscarPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
    }

    private void registrarLog(Long usuarioId, String accion, String detalles) {
        java.util.Map<String, Object> logMap = new java.util.HashMap<>();
        logMap.put("microservicio", "pedido-service");
        logMap.put("accion", accion);
        logMap.put("usuarioId", usuarioId);
        logMap.put("detalles", detalles);
        logMap.put("fecha", LocalDateTime.now());

        try {
            restTemplate.postForEntity("http://localhost:8084/api/analitica/logs", logMap, String.class);
        } catch (Exception e) {
            log.warn("Error al enviar log a analítica", e);
        }
    }
}
