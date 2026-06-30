package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.model.EstadoPedido;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("PedidoController")
class PedidoControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean PedidoService pedidoService;

    @SuppressWarnings("unused")
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

    private EstadoPedido estado(String nombre) {
        EstadoPedido e = new EstadoPedido();
        e.setId(1L);
        e.setNombre(nombre);
        return e;
    }

    private Pedido pedido(Long id) {
        return Pedido.builder()
                .id(id)
                .clienteId(5L)
                .subtotal(50000.0)
                .total(50000.0)
                .estado(estado("PENDIENTE"))
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("POST /generar")
    class GenerarPedido {

        @Test
        @DisplayName("200 OK al generar pedido sin dirección específica")
        void exitoso() throws Exception {
            when(pedidoService.generarPedidoDesdeCarrito(eq(5L), isNull())).thenReturn(pedido(1L));

            mvc.perform(post("/api/pedidos/generar")
                    .header("X-User-Id", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.clienteId").value(5))
                    .andExpect(jsonPath("$.estado.nombre").value("PENDIENTE"));
        }

        @Test
        @DisplayName("200 OK al generar pedido con dirección específica")
        void exitosoConDireccion() throws Exception {
            when(pedidoService.generarPedidoDesdeCarrito(eq(5L), eq(10L))).thenReturn(pedido(1L));

            mvc.perform(post("/api/pedidos/generar/10")
                    .header("X-User-Id", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("400 si el carrito está vacío")
        void carritoVacio() throws Exception {
            when(pedidoService.generarPedidoDesdeCarrito(eq(5L), isNull()))
                    .thenThrow(new RuntimeException("El carrito está vacío o no existe."));

            mvc.perform(post("/api/pedidos/generar")
                    .header("X-User-Id", "5"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /{pedidoId}/estado/{estadoId}")
    class ActualizarEstado {

        @Test
        @DisplayName("200 OK al actualizar estado")
        void exitoso() throws Exception {
            Pedido actualizado = pedido(1L);
            actualizado.setEstado(estado("CONFIRMADO"));
            when(pedidoService.actualizarEstado(1L, 2L)).thenReturn(actualizado);

            mvc.perform(put("/api/pedidos/1/estado/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado.nombre").value("CONFIRMADO"));
        }

        @Test
        @DisplayName("400 si el estado no existe")
        void estadoNoExiste() throws Exception {
            when(pedidoService.actualizarEstado(1L, 99L))
                    .thenThrow(new RuntimeException("Estado no encontrado"));

            mvc.perform(put("/api/pedidos/1/estado/99"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /{pedidoId}/estado-nombre")
    class ActualizarEstadoPorNombre {

        @Test
        @DisplayName("200 OK al actualizar estado por nombre")
        void exitoso() throws Exception {
            Pedido actualizado = pedido(1L);
            actualizado.setEstado(estado("CONFIRMADO"));
            when(pedidoService.actualizarEstadoPorNombre(1L, "CONFIRMADO")).thenReturn(actualizado);

            mvc.perform(put("/api/pedidos/1/estado-nombre")
                            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                            .content("CONFIRMADO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado.nombre").value("CONFIRMADO"));
        }

        @Test
        @DisplayName("400 si el estado no existe")
        void estadoNoExiste() throws Exception {
            when(pedidoService.actualizarEstadoPorNombre(1L, "INEXISTENTE"))
                    .thenThrow(new RuntimeException("Estado no encontrado"));

            mvc.perform(put("/api/pedidos/1/estado-nombre")
                            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                            .content("INEXISTENTE"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /cliente/{clienteId}")
    class ObtenerHistorial {

        @Test
        @DisplayName("200 OK con lista de pedidos")
        void exitoso() throws Exception {
            when(pedidoService.obtenerHistorialCliente(5L)).thenReturn(List.of(pedido(1L)));

            mvc.perform(get("/api/pedidos/cliente/5")
                    .header("X-User-Id", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].clienteId").value(5));
        }

        @Test
        @DisplayName("200 OK con lista vacía")
        void sinPedidos() throws Exception {
            when(pedidoService.obtenerHistorialCliente(99L)).thenReturn(List.of());

            mvc.perform(get("/api/pedidos/cliente/99")
                    .header("X-User-Id", "99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /{pedidoId}")
    class ObtenerPedido {

        @Test
        @DisplayName("200 OK al obtener pedido existente")
        void exitoso() throws Exception {
            when(pedidoService.buscarPorId(1L)).thenReturn(pedido(1L));

            mvc.perform(get("/api/pedidos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("400 si el pedido no existe")
        void noExiste() throws Exception {
            when(pedidoService.buscarPorId(99L))
                    .thenThrow(new RuntimeException("Pedido no encontrado"));

            mvc.perform(get("/api/pedidos/99"))
                    .andExpect(status().isBadRequest());
        }
    }
}
