package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.client.AnaliticaClient;
import com.ecomarket.pedidos.client.CarritoCompraClient;
import com.ecomarket.pedidos.client.CatalogoInventarioClient;
import com.ecomarket.pedidos.client.RegistroUsuariosClient;
import com.ecomarket.pedidos.dto.CarritoDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;
import com.ecomarket.pedidos.model.*;
import com.ecomarket.pedidos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final RegistroUsuariosClient registroUsuariosClient;
    private final CarritoCompraClient carritoCompraClient;
    private final CatalogoInventarioClient catalogoInventarioClient;
    private final AnaliticaClient analiticaClient;
    private final RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    @Transactional
    public Pedido generarPedidoDesdeCarrito(Long clienteId, Long direccionEnvioId) {
        registroUsuariosClient.obtenerUsuario(clienteId);

        CarritoDTO carrito = carritoCompraClient.obtenerCarrito(clienteId);
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new NoExisteEnBdException("El carrito del cliente " + clienteId + " está vacío.");
        }
        if (carrito.getMetodoPagoId() == null) {
            throw new NoExisteEnBdException("Debe seleccionar un método de pago antes de generar el pedido.");
        }
        if (carrito.getMetodoEnvioId() == null) {
            throw new NoExisteEnBdException("Debe seleccionar un método de envío antes de generar el pedido.");
        }

        Long finalDireccionId = direccionEnvioId;
        if (finalDireccionId == null) {
            try {
                Map<String, Object> userDir = restTemplate.getForObject("http://localhost:8081/api/usuarios/direcciones/predeterminada/" + clienteId, Map.class);
                if (userDir != null && userDir.get("id") != null) {
                    finalDireccionId = Long.valueOf(userDir.get("id").toString());
                } else {
                    throw new NoExisteEnBdException("El usuario no tiene una dirección predeterminada configurada.");
                }
            } catch (Exception e) {
                throw new NoExisteEnBdException("No se pudo obtener la dirección predeterminada del usuario.");
            }
        }

        for (var itemDto : carrito.getItems()) {
            catalogoInventarioClient.obtenerProducto(itemDto.getProductoId());
        }

        EstadoPedido estadoInicial = estadoPedidoRepository.findById(1L)
                .orElseThrow(() -> new NoExisteEnBdException("No se encontró el estado inicial del pedido."));

        Pedido pedido = Pedido.builder()
                .clienteId(clienteId)
                .carritoId(carrito.getId())
                .direccionEnvioId(finalDireccionId)
                .metodoPagoId(carrito.getMetodoPagoId())
                .subtotal(carrito.getSubtotal())
                .total(carrito.getSubtotal())
                .estado(estadoInicial)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        List<ItemPedido> items = carrito.getItems().stream().map(itemDto ->
            ItemPedido.builder()
                .pedidoId(pedidoGuardado.getId())
                .productoId(itemDto.getProductoId())
                .cantidad(itemDto.getCantidad())
                .precioUnitarioHistorico(itemDto.getPrecioUnitarioAgregado())
                .build()
        ).collect(Collectors.toList());

        itemPedidoRepository.saveAll(items);
        carritoCompraClient.cerrarCarrito(clienteId);
        carritoCompraClient.vaciarCarrito(clienteId);

        java.util.Map<String, Object> log = new java.util.HashMap<>();
        log.put("microservicio", "pedido-service");
        log.put("accion", "PEDIDO_GENERADO");
        log.put("usuarioId", clienteId);
        log.put("detalles", "Pedido generado exitosamente con ID: " + pedidoGuardado.getId());
        log.put("fecha", LocalDateTime.now());
        analiticaClient.registrarLog(log);

        return pedidoGuardado;
    }

    @Transactional
    public Pedido actualizarEstado(Long pedidoId, Long nuevoEstadoId) {
        Pedido pedido = buscarPorId(pedidoId);
        EstadoPedido estado = estadoPedidoRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new NoExisteEnBdException("Estado de pedido no encontrado con ID: " + nuevoEstadoId));
        
        if (nuevoEstadoId == 4L) {
            dispararCreacionEnvio(pedido);
        }
        
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    private void dispararCreacionEnvio(Pedido pedido) {
        try {
            String url = "http://localhost:8083/api/v1/logistica-envios/envios/auto/" + pedido.getId();
            restTemplate.postForEntity(url, null, String.class);
            log.info("Envío creado automáticamente para el pedido {}", pedido.getId());
        } catch (Exception e) {
            log.error("Error al disparar la creación automática del envío para el pedido {}: {}", pedido.getId(), e.getMessage());
        }
    }

    public List<Pedido> obtenerHistorialCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido buscarPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NoExisteEnBdException("Pedido no encontrado con ID: " + pedidoId));
    }

    @Transactional
    public Pedido actualizarEstadoPorNombre(Long pedidoId, String nombreEstado) {
        Pedido pedido = buscarPorId(pedidoId);
        EstadoPedido estado = estadoPedidoRepository.findByNombre(nombreEstado)
                .orElseThrow(() -> new NoExisteEnBdException("Estado de pedido no encontrado con nombre: " + nombreEstado));
        
        if ("ENVIADO".equalsIgnoreCase(nombreEstado)) {
            dispararCreacionEnvio(pedido);
        }
        
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void actualizarEstadoPorEnvio(Long pedidoId, String estadoEnvioNombre) {
        String estadoPedidoNombre = switch (estadoEnvioNombre.toLowerCase().replace("ó", "o")) {
            case "en_transito", "en_tránsito" -> "EN_TRANSITO";
            case "entregado" -> "ENTREGADO";
            default -> null;
        };
        if (estadoPedidoNombre != null) {
            actualizarEstadoPorNombre(pedidoId, estadoPedidoNombre);
        }
    }
}
