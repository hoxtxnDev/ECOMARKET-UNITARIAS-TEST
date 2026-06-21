package com.ecomarket.carritocompraservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.carritocompraservice.model.Carrito;
import com.ecomarket.carritocompraservice.service.CarritoService;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CarritoService carritoService;

    @Test
    void listarCarritosReturnsOk() throws Exception {
        when(carritoService.listarTodos()).thenReturn(List.of(new Carrito()));

        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void obtenerCarritoReturnsOk() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setClienteId(10L);
        when(carritoService.obtenerCarritoActivo(10L)).thenReturn(carrito);

        mockMvc.perform(get("/api/carrito/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(10));
    }

    @Test
    void anadirProductoReturnsOk() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoService.anadirProducto(10L, 100L, 2)).thenReturn(carrito);

        mockMvc.perform(post("/api/carrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId":10,"productoId":100,"cantidad":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void removerProductoReturnsOk() throws Exception {
        when(carritoService.removerProducto(10L, 5L)).thenReturn(new Carrito());

        mockMvc.perform(delete("/api/carrito/10/item/5"))
                .andExpect(status().isOk());
    }

    @Test
    void seleccionarPagoReturnsOk() throws Exception {
        when(carritoService.seleccionarMetodoPago(10L, 3L)).thenReturn(new Carrito());

        mockMvc.perform(put("/api/carrito/10/pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    void seleccionarEnvioReturnsOk() throws Exception {
        when(carritoService.seleccionarEnvio(10L, 2L)).thenReturn(new Carrito());

        mockMvc.perform(put("/api/carrito/10/envio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void vaciarCarritoReturnsOk() throws Exception {
        when(carritoService.vaciarCarrito(10L)).thenReturn(true);

        mockMvc.perform(delete("/api/carrito/10/vaciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void iniciarCompraReturnsOk() throws Exception {
        when(carritoService.iniciarProcesoCompra(10L)).thenReturn(1L);

        mockMvc.perform(post("/api/carrito/10/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void cerrarCarritoReturnsOk() throws Exception {
        mockMvc.perform(put("/api/carrito/10/cerrar"))
                .andExpect(status().isOk());
    }
}
