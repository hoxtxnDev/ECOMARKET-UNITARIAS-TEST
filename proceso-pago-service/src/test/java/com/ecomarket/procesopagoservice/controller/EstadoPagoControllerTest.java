package com.ecomarket.procesopagoservice.controller;

import com.ecomarket.procesopagoservice.model.EstadoPago;
import com.ecomarket.procesopagoservice.repository.EstadoPagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstadoPagoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("EstadoPagoController")
class EstadoPagoControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean EstadoPagoRepository repository;

    private EstadoPago estado(Long id, String nombre) {
        EstadoPago e = new EstadoPago();
        e.setId(id);
        e.setNombre(nombre);
        return e;
    }

    @Nested
    @DisplayName("GET /api/estado-pago")
    class Listar {

        @Test
        @DisplayName("200 OK retorna lista de estados")
        void exitoso() throws Exception {
            when(repository.findAll()).thenReturn(List.of(
                    estado(1L, "PENDIENTE"), estado(2L, "APROBADO")
            ));

            mvc.perform(get("/api/estado-pago"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].nombre").value("PENDIENTE"));
        }
    }

    @Nested
    @DisplayName("GET /api/estado-pago/{id}")
    class Obtener {

        @Test
        @DisplayName("200 OK cuando existe")
        void exitoso() throws Exception {
            when(repository.findById(1L)).thenReturn(Optional.of(estado(1L, "APROBADO")));

            mvc.perform(get("/api/estado-pago/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("APROBADO"));
        }

        @Test
        @DisplayName("404 cuando no existe")
        void noExiste() throws Exception {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/estado-pago/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/estado-pago")
    class Crear {

        @Test
        @DisplayName("201 Created al crear estado")
        void exitoso() throws Exception {
            when(repository.save(any())).thenReturn(estado(1L, "NUEVO"));

            mvc.perform(post("/api/estado-pago")
                            .contentType("application/json")
                            .content("{\"nombre\": \"NUEVO\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("NUEVO"));
        }
    }

    @Nested
    @DisplayName("PUT /api/estado-pago/{id}")
    class Actualizar {

        @Test
        @DisplayName("200 OK al actualizar")
        void exitoso() throws Exception {
            when(repository.findById(1L)).thenReturn(Optional.of(estado(1L, "VIEJO")));
            when(repository.save(any())).thenReturn(estado(1L, "ACTUALIZADO"));

            mvc.perform(put("/api/estado-pago/1")
                            .contentType("application/json")
                            .content("{\"nombre\": \"ACTUALIZADO\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("ACTUALIZADO"));
        }

        @Test
        @DisplayName("404 si no existe")
        void noExiste() throws Exception {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            mvc.perform(put("/api/estado-pago/99")
                            .contentType("application/json")
                            .content("{\"nombre\": \"NUEVO\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/estado-pago/{id}")
    class Eliminar {

        @Test
        @DisplayName("204 No Content al eliminar existente")
        void exitoso() throws Exception {
            when(repository.existsById(1L)).thenReturn(true);

            mvc.perform(delete("/api/estado-pago/1"))
                    .andExpect(status().isNoContent());
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("404 si no existe")
        void noExiste() throws Exception {
            when(repository.existsById(99L)).thenReturn(false);

            mvc.perform(delete("/api/estado-pago/99"))
                    .andExpect(status().isNotFound());
            verify(repository, never()).deleteById(any());
        }
    }
}
