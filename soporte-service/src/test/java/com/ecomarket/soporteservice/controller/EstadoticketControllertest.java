package com.ecomarket.soporteservice.controller;

import com.ecomarket.soporteservice.exception.GlobalExceptionHandler;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.exception.YaExisteEnBdException;
import com.ecomarket.soporteservice.model.reference.EstadoTicket;
import com.ecomarket.soporteservice.service.EstadoTicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstadoTicketController")
class EstadoTicketControllerTest {

    MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock EstadoTicketService service;

    @BeforeEach
    void setup() {
        mvc = standaloneSetup(new EstadoTicketController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private EstadoTicket estado(Long id, String nombre) {
        return new EstadoTicket(id, nombre);
    }

    @Test
    @DisplayName("GET /api/v1/estado-ticket → 200 lista")
    void getAll() throws Exception {
        when(service.readAllEstadoTicket()).thenReturn(List.of(estado(1L, "ABIERTO"), estado(2L, "EN_PROCESO")));
        mvc.perform(get("/api/v1/estado-ticket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/v1/estado-ticket → 201 creado")
    void postEstado() throws Exception {
        when(service.createEstadoTicket(any())).thenReturn(estado(3L, "RESUELTO"));
        mvc.perform(post("/api/v1/estado-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(estado(null, "RESUELTO"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("POST nombre vacío → 400")
    void postEstadoInvalido() throws Exception {
        mvc.perform(post("/api/v1/estado-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(estado(null, ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST duplicado → 409 (YaExisteEnBdException)")
    void postDuplicado() throws Exception {
        when(service.createEstadoTicket(any())).thenThrow(new YaExisteEnBdException("ABIERTO ya existe."));
        mvc.perform(post("/api/v1/estado-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(estado(null, "ABIERTO"))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/v1/estado-ticket/1 → 200")
    void deleteExistente() throws Exception {
        doNothing().when(service).deleteEstadoTicket(1L);
        mvc.perform(delete("/api/v1/estado-ticket/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("DELETE inexistente → 404 (NoExisteEnBdException)")
    void deleteInexistente() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).deleteEstadoTicket(99L);
        mvc.perform(delete("/api/v1/estado-ticket/99"))
                .andExpect(status().isNotFound());
    }
}
