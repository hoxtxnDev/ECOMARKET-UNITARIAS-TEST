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
    void obtenerCarritoActivoReturnsOk() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setClienteId(10L);
        when(carritoService.obtenerCarritoActivo(10L)).thenReturn(carrito);

        mockMvc.perform(get("/api/carrito/activo")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(10));
    }

    @Test
    void anadirProductoReturnsOk() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoService.anadirProducto(10L, 100L, 2)).thenReturn(carrito);

        mockMvc.perform(post("/api/carrito")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productoId":100,"cantidad":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void removerProductoReturnsOk() throws Exception {
        when(carritoService.removerProducto(10L, 5L)).thenReturn(new Carrito());

        mockMvc.perform(delete("/api/carrito/item/5")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void seleccionarPagoReturnsOk() throws Exception {
        when(carritoService.seleccionarMetodoPago(10L, 3L)).thenReturn(new Carrito());

        mockMvc.perform(put("/api/carrito/pago")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    void seleccionarEnvioReturnsOk() throws Exception {
        when(carritoService.seleccionarEnvio(10L, 2L)).thenReturn(new Carrito());

        mockMvc.perform(put("/api/carrito/envio")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void vaciarCarritoReturnsOk() throws Exception {
        when(carritoService.vaciarCarrito(10L)).thenReturn(true);

        mockMvc.perform(delete("/api/carrito/vaciar")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void cerrarCarritoReturnsOk() throws Exception {
        mockMvc.perform(put("/api/carrito/cerrar")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk());
    }
}
