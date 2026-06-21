package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.model.EstadoPedido;
import com.ecomarket.pedidos.repository.EstadoPedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstadoPedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EstadoPedidoController")
class EstadoPedidoControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean EstadoPedidoRepository repository;

    private EstadoPedido estado(Long id, String nombre) {
        return EstadoPedido.builder().id(id).nombre(nombre).build();
    }

    @Nested
    @DisplayName("GET /api/estado-pedido")
    class Listar {

        @Test
        @DisplayName("200 OK con lista de estados")
        void exitoso() throws Exception {
            when(repository.findAll()).thenReturn(List.of(estado(1L, "PENDIENTE"), estado(2L, "CONFIRMADO")));

            mvc.perform(get("/api/estado-pedido"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].nombre").value("PENDIENTE"));
        }
    }

    @Nested
    @DisplayName("GET /api/estado-pedido/{id}")
    class Obtener {

        @Test
        @DisplayName("200 OK cuando existe")
        void exitoso() throws Exception {
            when(repository.findById(1L)).thenReturn(Optional.of(estado(1L, "PENDIENTE")));

            mvc.perform(get("/api/estado-pedido/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("PENDIENTE"));
        }

        @Test
        @DisplayName("404 cuando no existe")
        void noExiste() throws Exception {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/estado-pedido/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/estado-pedido")
    class Crear {

        @Test
        @DisplayName("201 Created al crear estado")
        void exitoso() throws Exception {
            EstadoPedido creado = estado(1L, "NUEVO");
            when(repository.save(any())).thenReturn(creado);

            mvc.perform(post("/api/estado-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombre\":\"NUEVO\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("NUEVO"));
        }
    }

    @Nested
    @DisplayName("PUT /api/estado-pedido/{id}")
    class Actualizar {

        @Test
        @DisplayName("200 OK al actualizar estado existente")
        void exitoso() throws Exception {
            EstadoPedido existente = estado(1L, "PENDIENTE");
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            mvc.perform(put("/api/estado-pedido/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombre\":\"CONFIRMADO\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("CONFIRMADO"));
        }

        @Test
        @DisplayName("404 cuando el estado no existe")
        void noExiste() throws Exception {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            mvc.perform(put("/api/estado-pedido/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombre\":\"INEXISTENTE\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/estado-pedido/{id}")
    class Eliminar {

        @Test
        @DisplayName("204 No Content al eliminar existente")
        void exitoso() throws Exception {
            when(repository.existsById(1L)).thenReturn(true);

            mvc.perform(delete("/api/estado-pedido/1"))
                    .andExpect(status().isNoContent());
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("404 cuando el estado no existe")
        void noExiste() throws Exception {
            when(repository.existsById(99L)).thenReturn(false);

            mvc.perform(delete("/api/estado-pedido/99"))
                    .andExpect(status().isNotFound());
            verify(repository, never()).deleteById(any());
        }
    }
}
