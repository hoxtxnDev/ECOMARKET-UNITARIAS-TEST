package com.ecomarket.carritocompraservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecomarket.carritocompraservice.model.Carrito;
import com.ecomarket.carritocompraservice.model.EstadoPedido;
import com.ecomarket.carritocompraservice.model.ItemCarrito;
import com.ecomarket.carritocompraservice.model.Pedido;
import com.ecomarket.carritocompraservice.repository.CarritoRepository;
import com.ecomarket.carritocompraservice.repository.EstadoPedidoRepository;
import com.ecomarket.carritocompraservice.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private CarritoRepository carritoRepository;
    @Mock private EstadoPedidoRepository estadoPedidoRepository;

    @InjectMocks private PedidoService pedidoService;

    private Carrito carrito;
    private EstadoPedido estadoInicial;

    @BeforeEach
    void setUp() {
        estadoInicial = new EstadoPedido(1L, "Pendiente");
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setClienteId(10L);
        carrito.setItems(new ArrayList<>());
    }

    @Test
    void generarPedidoDesdeCarritoCreatesPedido() {
        ItemCarrito item = new ItemCarrito(1L, carrito, 100L, 2, 25.0);
        carrito.setItems(List.of(item));

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(estadoPedidoRepository.findById(1L)).thenReturn(Optional.of(estadoInicial));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido result = pedidoService.generarPedidoDesdeCarrito(10L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getClienteId());
        assertEquals(estadoInicial, result.getEstado());
        assertEquals(50.0, result.getSubtotal());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void generarPedidoDesdeCarritoThrowsWhenCarritoNotFound() {
        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.generarPedidoDesdeCarrito(10L, 99L));
    }

    @Test
    void generarPedidoDesdeCarritoThrowsWhenEstadoNotFound() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(estadoPedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.generarPedidoDesdeCarrito(10L, 1L));
    }

    @Test
    void actualizarEstadoUpdatesPedido() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(new EstadoPedido(1L, "Pendiente"));

        EstadoPedido nuevoEstado = new EstadoPedido(2L, "Enviado");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(2L)).thenReturn(Optional.of(nuevoEstado));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido result = pedidoService.actualizarEstado(1L, 2L);

        assertEquals(nuevoEstado, result.getEstado());
    }

    @Test
    void actualizarEstadoThrowsWhenPedidoNotFound() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.actualizarEstado(99L, 1L));
    }

    @Test
    void actualizarEstadoThrowsWhenEstadoNotFound() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.actualizarEstado(1L, 99L));
    }

    @Test
    void obtenerHistorialClienteReturnsList() {
        Pedido pedido = new Pedido();
        when(pedidoRepository.findByClienteId(10L)).thenReturn(List.of(pedido));

        List<Pedido> result = pedidoService.obtenerHistorialCliente(10L);

        assertEquals(1, result.size());
    }

    @Test
    void buscarPorIdReturnsPedidoWhenFound() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido result = pedidoService.buscarPorId(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void buscarPorIdThrowsWhenNotFound() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.buscarPorId(99L));
    }

    @Test
    void listarTodosReturnsAll() {
        when(pedidoRepository.findAll()).thenReturn(List.of(new Pedido()));

        assertEquals(1, pedidoService.listarTodos().size());
    }
}
