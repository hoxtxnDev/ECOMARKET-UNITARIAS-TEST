package com.ecomarket.procesopagoservice.controller;

import com.ecomarket.procesopagoservice.model.MetodoPagoTransaccion;
import com.ecomarket.procesopagoservice.repository.MetodoPagoRepository;
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

@WebMvcTest(MetodoPagoTransaccionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("MetodoPagoTransaccionController")
class MetodoPagoTransaccionControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean MetodoPagoRepository repository;

    private MetodoPagoTransaccion metodo(Long id, String nombre) {
        MetodoPagoTransaccion m = new MetodoPagoTransaccion();
        m.setId(id);
        m.setNombre(nombre);
        return m;
    }

    @Nested
    @DisplayName("GET /api/metodo-pago")
    class Listar {
        @Test
        @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(repository.findAll()).thenReturn(List.of(
                    metodo(1L, "TARJETA"), metodo(2L, "TRANSFERENCIA")
            ));
            mvc.perform(get("/api/metodo-pago"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/metodo-pago/{id}")
    class Obtener {
        @Test
        @DisplayName("200 OK cuando existe")
        void exitoso() throws Exception {
            when(repository.findById(1L)).thenReturn(Optional.of(metodo(1L, "TARJETA")));
            mvc.perform(get("/api/metodo-pago/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("TARJETA"));
        }

        @Test
        @DisplayName("404 cuando no existe")
        void noExiste() throws Exception {
            when(repository.findById(99L)).thenReturn(Optional.empty());
            mvc.perform(get("/api/metodo-pago/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/metodo-pago")
    class Crear {
        @Test
        @DisplayName("201 Created")
        void exitoso() throws Exception {
            when(repository.save(any())).thenReturn(metodo(1L, "DEBITO"));
            mvc.perform(post("/api/metodo-pago")
                            .contentType("application/json")
                            .content("{\"nombre\": \"DEBITO\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nombre").value("DEBITO"));
        }
    }

    @Nested
    @DisplayName("PUT /api/metodo-pago/{id}")
    class Actualizar {
        @Test
        @DisplayName("200 OK al actualizar")
        void exitoso() throws Exception {
            when(repository.findById(1L)).thenReturn(Optional.of(metodo(1L, "TARJETA")));
            when(repository.save(any())).thenReturn(metodo(1L, "CREDITO"));
            mvc.perform(put("/api/metodo-pago/1")
                            .contentType("application/json")
                            .content("{\"nombre\": \"CREDITO\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("CREDITO"));
        }

        @Test
        @DisplayName("404 si no existe")
        void noExiste() throws Exception {
            when(repository.findById(99L)).thenReturn(Optional.empty());
            mvc.perform(put("/api/metodo-pago/99")
                            .contentType("application/json")
                            .content("{\"nombre\": \"NUEVO\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/metodo-pago/{id}")
    class Eliminar {
        @Test
        @DisplayName("204 No Content")
        void exitoso() throws Exception {
            when(repository.existsById(1L)).thenReturn(true);
            mvc.perform(delete("/api/metodo-pago/1"))
                    .andExpect(status().isNoContent());
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("404 si no existe")
        void noExiste() throws Exception {
            when(repository.existsById(99L)).thenReturn(false);
            mvc.perform(delete("/api/metodo-pago/99"))
                    .andExpect(status().isNotFound());
            verify(repository, never()).deleteById(any());
        }
    }
}
