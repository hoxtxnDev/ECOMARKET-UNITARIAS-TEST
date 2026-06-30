package com.ecomarket.soporteservice.controller;

import com.ecomarket.soporteservice.dto.ResenaRequestDTO;
import com.ecomarket.soporteservice.exception.GlobalExceptionHandler;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.model.entity.Resena;
import com.ecomarket.soporteservice.service.ResenaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResenaController")
class ResenaControllerTest {

    MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock ResenaService service;

    @BeforeEach
    void setup() {
        mvc = standaloneSetup(new ResenaController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Resena resena(Long id) {
        Resena r = new Resena();
        r.setId(id);
        r.setProductoId(10L);
        r.setClienteId(5L);
        r.setCalificacionEstrellas(8);
        r.setComentario("Muy buen producto, lo recomiendo");
        r.setFechaPublicacion(LocalDateTime.now());
        r.setModeracionAprobado(false);
        return r;
    }

    private ResenaRequestDTO dto() {
        ResenaRequestDTO d = new ResenaRequestDTO();
        d.setProductoId(10L);
        d.setClienteId(5L);
        d.setCalificacionEstrellas(4);
        d.setComentario("Muy buen producto, lo recomiendo");
        return d;
    }

    @Test
    @DisplayName("GET /api/v1/resenas sin param → 200 lista completa")
    void getAllSinParam() throws Exception {
        when(service.readAllResenas()).thenReturn(List.of(resena(1L), resena(2L)));
        mvc.perform(get("/api/v1/resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/resenas?productoId=10 → 200 por producto")
    void getAllPorProducto() throws Exception {
        when(service.readResenasByProductoId(10L)).thenReturn(List.of(resena(1L)));
        mvc.perform(get("/api/v1/resenas").param("productoId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/resenas?clienteId=5 → 200 por cliente")
    void getAllPorCliente() throws Exception {
        when(service.readResenasByClienteId(5L)).thenReturn(List.of(resena(1L)));
        mvc.perform(get("/api/v1/resenas").param("clienteId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/resenas/{id} → 200 por id")
    void getById() throws Exception {
        when(service.findResenaById(1L)).thenReturn(resena(1L));
        mvc.perform(get("/api/v1/resenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /{id} inexistente → 404")
    void getByIdNoExiste() throws Exception {
        when(service.findResenaById(99L)).thenThrow(new NoExisteEnBdException("99 no existe."));
        mvc.perform(get("/api/v1/resenas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/resenas → 201 reseña creada")
    void dejarResena() throws Exception {
        when(service.dejarResena(10L, 5L, 4, "Muy buen producto, lo recomiendo")).thenReturn(resena(1L));
        mvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST comentario vacío → 400")
    void dejarResenaInvalida() throws Exception {
        ResenaRequestDTO invalido = new ResenaRequestDTO();
        invalido.setProductoId(10L);
        invalido.setClienteId(5L);
        invalido.setCalificacionEstrellas(3);
        invalido.setComentario("");
        mvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/resenas/1/aprobar → 200")
    void aprobarResena() throws Exception {
        doNothing().when(service).aprobarModeracion(1L);
        mvc.perform(patch("/api/v1/resenas/1/aprobar"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("aprobada")));
    }

    @Test
    @DisplayName("PATCH aprobar inexistente → 404")
    void aprobarResenaNoExiste() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).aprobarModeracion(99L);
        mvc.perform(patch("/api/v1/resenas/99/aprobar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/v1/resenas/1/rechazar → 200")
    void rechazarResena() throws Exception {
        doNothing().when(service).rechazarModeracion(1L);
        mvc.perform(patch("/api/v1/resenas/1/rechazar"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("rechazada")));
    }

    @Test
    @DisplayName("PATCH rechazar inexistente → 404")
    void rechazarResenaNoExiste() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).rechazarModeracion(99L);
        mvc.perform(patch("/api/v1/resenas/99/rechazar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/resenas/1 → 200")
    void eliminarResena() throws Exception {
        doNothing().when(service).deleteResenaById(1L);
        mvc.perform(delete("/api/v1/resenas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("DELETE inexistente → 404")
    void eliminarResenaNoExiste() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).deleteResenaById(99L);
        mvc.perform(delete("/api/v1/resenas/99"))
                .andExpect(status().isNotFound());
    }
}
