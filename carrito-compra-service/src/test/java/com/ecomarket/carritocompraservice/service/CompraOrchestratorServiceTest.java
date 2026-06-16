package com.ecomarket.carritocompraservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecomarket.carritocompraservice.client.LogisticaEnvioClient;
import com.ecomarket.carritocompraservice.client.ProcesoPagoClient;
import com.ecomarket.carritocompraservice.dto.CompraRequestDTO;
import com.ecomarket.carritocompraservice.dto.CompraResultDTO;
import com.ecomarket.carritocompraservice.model.Carrito;
import com.ecomarket.carritocompraservice.model.Pedido;
import com.ecomarket.carritocompraservice.repository.CarritoRepository;

@ExtendWith(MockitoExtension.class)
class CompraOrchestratorServiceTest {

    @Mock private CarritoService carritoService;
    @Mock private PedidoService pedidoService;
    @Mock private CarritoRepository carritoRepository;
    @Mock private LogisticaEnvioClient envioClient;
    @Mock private ProcesoPagoClient pagoClient;

    @InjectMocks private CompraOrchestratorService service;

    private CompraRequestDTO request() {
        return new CompraRequestDTO(10L, 2L, 3L, 4L);
    }

    @Test
    void ejecutarCompraCompletesSuccessfully() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setTotal(500.0);

        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(pedidoService.generarPedidoDesdeCarrito(10L, 1L)).thenReturn(pedido);
        when(pagoClient.iniciarPago(100L, 10L, 500.0, 4L)).thenReturn(888L);
        when(envioClient.crearEnvio(100L, 10L, 2L, 3L)).thenReturn(999L);

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("COMPLETADO", result.getEstado());
        assertEquals(1L, result.getCarritoId());
        assertEquals(pedido, result.getPedido());
        assertEquals(888L, result.getTransaccionPagoId());
        assertEquals(999L, result.getEnvioId());
    }

    @Test
    void ejecutarCompraReturnsErrorWhenCarritoInitFails() {
        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(null);

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("ERROR: No se pudo iniciar el proceso de compra", result.getEstado());
    }

    @Test
    void ejecutarCompraReturnsErrorWhenCarritoNotFound() {
        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.empty());

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("ERROR: Carrito no encontrado", result.getEstado());
    }

    @Test
    void ejecutarCompraReturnsErrorWhenPedidoGenerationFails() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(pedidoService.generarPedidoDesdeCarrito(10L, 1L)).thenReturn(null);

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("ERROR: No se pudo generar el pedido", result.getEstado());
    }

    @Test
    void ejecutarCompraContinuesWhenPagoFails() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setTotal(500.0);

        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(pedidoService.generarPedidoDesdeCarrito(10L, 1L)).thenReturn(pedido);
        when(pagoClient.iniciarPago(100L, 10L, 500.0, 4L)).thenReturn(null);
        when(envioClient.crearEnvio(100L, 10L, 2L, 3L)).thenReturn(999L);

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("COMPLETADO", result.getEstado());
        assertNull(result.getTransaccionPagoId());
        assertEquals(999L, result.getEnvioId());
    }

    @Test
    void ejecutarCompraContinuesWhenEnvioFails() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setTotal(500.0);

        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(pedidoService.generarPedidoDesdeCarrito(10L, 1L)).thenReturn(pedido);
        when(pagoClient.iniciarPago(100L, 10L, 500.0, 4L)).thenReturn(888L);
        when(envioClient.crearEnvio(100L, 10L, 2L, 3L)).thenReturn(null);

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("COMPLETADO", result.getEstado());
        assertEquals(888L, result.getTransaccionPagoId());
        assertNull(result.getEnvioId());
    }

    @Test
    void ejecutarCompraUsesZeroWhenPedidoTotalIsNull() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setTotal(null);

        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(pedidoService.generarPedidoDesdeCarrito(10L, 1L)).thenReturn(pedido);
        when(pagoClient.iniciarPago(100L, 10L, 0.0, 4L)).thenReturn(888L);
        when(envioClient.crearEnvio(100L, 10L, 2L, 3L)).thenReturn(999L);

        CompraResultDTO result = service.ejecutarCompra(request());

        assertEquals("COMPLETADO", result.getEstado());
        assertEquals(888L, result.getTransaccionPagoId());
    }
}
