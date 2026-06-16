package com.ecomarket.carritocompraservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.carritocompraservice.model.EstadoPedido;
import com.ecomarket.carritocompraservice.model.Pedido;
import com.ecomarket.carritocompraservice.service.PedidoService;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PedidoService pedidoService;

    private Pedido pedido() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setClienteId(10L);
        p.setEstado(new EstadoPedido(1L, "Pendiente"));
        return p;
    }

    @Test
    void generarPedidoReturnsOk() throws Exception {
        when(pedidoService.generarPedidoDesdeCarrito(10L, 1L)).thenReturn(pedido());

        mockMvc.perform(post("/api/pedido/generar?clienteId=10&carritoId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(10));
    }

    @Test
    void actualizarEstadoReturnsOk() throws Exception {
        when(pedidoService.actualizarEstado(1L, 2L)).thenReturn(pedido());

        mockMvc.perform(put("/api/pedido/1/estado?nuevoEstadoId=2"))
                .andExpect(status().isOk());
    }

    @Test
    void listarPedidosReturnsOk() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedido()));

        mockMvc.perform(get("/api/pedido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void historialClienteReturnsOk() throws Exception {
        when(pedidoService.obtenerHistorialCliente(10L)).thenReturn(List.of(pedido()));

        mockMvc.perform(get("/api/pedido/historial/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void buscarPedidoReturnsOk() throws Exception {
        when(pedidoService.buscarPorId(1L)).thenReturn(pedido());

        mockMvc.perform(get("/api/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(10));
    }
}
