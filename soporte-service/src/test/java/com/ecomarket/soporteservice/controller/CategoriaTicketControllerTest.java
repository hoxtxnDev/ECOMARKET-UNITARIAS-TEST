package com.ecomarket.soporteservice.controller;

import com.ecomarket.soporteservice.exception.GlobalExceptionHandler;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.exception.YaExisteEnBdException;
import com.ecomarket.soporteservice.model.reference.CategoriaTicket;
import com.ecomarket.soporteservice.service.CategoriaTicketService;
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
@DisplayName("CategoriaTicketController")
class CategoriaTicketControllerTest {

    MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock CategoriaTicketService service;

    @BeforeEach
    void setup() {
        mvc = standaloneSetup(new CategoriaTicketController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private CategoriaTicket cat(Long id, String nombre) {
        return new CategoriaTicket(id, nombre);
    }

    @Test
    @DisplayName("GET /api/v1/categoria-ticket → 200 lista")
    void getAll() throws Exception {
        when(service.readAllCategoriaTicket()).thenReturn(List.of(cat(1L, "ENTREGA"), cat(2L, "PAGO")));
        mvc.perform(get("/api/v1/categoria-ticket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/v1/categoria-ticket → 201 creado")
    void postCategoria() throws Exception {
        when(service.createCategoriaTicket(any())).thenReturn(cat(3L, "DEVOLUCION"));
        mvc.perform(post("/api/v1/categoria-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cat(null, "DEVOLUCION"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("POST nombre vacío → 400")
    void postCategoriaInvalida() throws Exception {
        mvc.perform(post("/api/v1/categoria-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cat(null, ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST duplicado → 409 (YaExisteEnBdException)")
    void postDuplicado() throws Exception {
        when(service.createCategoriaTicket(any())).thenThrow(new YaExisteEnBdException("ENTREGA ya existe."));
        mvc.perform(post("/api/v1/categoria-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cat(null, "ENTREGA"))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/v1/categoria-ticket/1 → 200")
    void deleteExistente() throws Exception {
        doNothing().when(service).deleteCategoriaTicketById(1L);
        mvc.perform(delete("/api/v1/categoria-ticket/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("DELETE inexistente → 404 (NoExisteEnBdException)")
    void deleteInexistente() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).deleteCategoriaTicketById(99L);
        mvc.perform(delete("/api/v1/categoria-ticket/99"))
                .andExpect(status().isNotFound());
    }
}
