package com.ecomarket.soporteservice.controller;

import com.ecomarket.soporteservice.dto.MensajeChatRequestDTO;
import com.ecomarket.soporteservice.exception.GlobalExceptionHandler;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.service.MensajeChatService;
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
@DisplayName("MensajeChatController")
class MensajeChatControllerTest {

    MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock MensajeChatService service;

    @BeforeEach
    void setup() {
        mvc = standaloneSetup(new MensajeChatController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private MensajeChat mensaje(Long id) {
        MensajeChat m = new MensajeChat();
        m.setId(id);
        m.setTicketId(1L);
        m.setRemitenteId(5L);
        m.setEsCliente(true);
        m.setContenido("Hola, necesito ayuda");
        m.setFechaEnvio(LocalDateTime.now());
        m.setLeido(false);
        return m;
    }

    private MensajeChatRequestDTO dto() {
        MensajeChatRequestDTO d = new MensajeChatRequestDTO();
        d.setTicketId(1L);
        d.setRemitenteId(5L);
        d.setEsCliente(true);
        d.setContenido("Hola, necesito ayuda");
        return d;
    }

    @Test
    @DisplayName("GET /api/v1/mensajes-chat sin param → 200 lista completa")
    void getAllSinParam() throws Exception {
        when(service.readAllMensajes()).thenReturn(List.of(mensaje(1L), mensaje(2L)));
        mvc.perform(get("/api/v1/mensajes-chat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/mensajes-chat?ticketId=1 → 200 historial del ticket")
    void getAllConTicketId() throws Exception {
        when(service.obtenerHistorialChat(1L)).thenReturn(List.of(mensaje(1L)));
        mvc.perform(get("/api/v1/mensajes-chat").param("ticketId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/mensajes-chat/{id} → 200 mensaje por id")
    void getById() throws Exception {
        when(service.findMensajeById(1L)).thenReturn(mensaje(1L));
        mvc.perform(get("/api/v1/mensajes-chat/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /{id} inexistente → 404")
    void getByIdNoExiste() throws Exception {
        when(service.findMensajeById(99L)).thenThrow(new NoExisteEnBdException("99 no existe."));
        mvc.perform(get("/api/v1/mensajes-chat/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/mensajes-chat → 201 mensaje enviado")
    void enviarMensaje() throws Exception {
        when(service.enviarMensajeChat(1L, 5L, true, "Hola, necesito ayuda")).thenReturn(mensaje(1L));
        mvc.perform(post("/api/v1/mensajes-chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST contenido vacío → 400")
    void enviarMensajeInvalido() throws Exception {
        MensajeChatRequestDTO invalido = new MensajeChatRequestDTO();
        invalido.setTicketId(1L);
        invalido.setRemitenteId(5L);
        invalido.setEsCliente(true);
        invalido.setContenido("");
        mvc.perform(post("/api/v1/mensajes-chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/mensajes-chat/1/leido → 200")
    void marcarComoLeido() throws Exception {
        doNothing().when(service).marcarComoLeido(1L);
        mvc.perform(patch("/api/v1/mensajes-chat/1/leido"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("PATCH /api/v1/mensajes-chat/1/leido inexistente → 404")
    void marcarComoLeidoNoExiste() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).marcarComoLeido(99L);
        mvc.perform(patch("/api/v1/mensajes-chat/99/leido"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/v1/mensajes-chat/marcar-leidos/1 → 200")
    void marcarTodosComoLeidos() throws Exception {
        doNothing().when(service).marcarTodosComoLeidos(1L);
        mvc.perform(patch("/api/v1/mensajes-chat/marcar-leidos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("DELETE /api/v1/mensajes-chat/1 → 200")
    void eliminarMensaje() throws Exception {
        doNothing().when(service).deleteMensajeById(1L);
        mvc.perform(delete("/api/v1/mensajes-chat/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("DELETE inexistente → 404")
    void eliminarMensajeNoExiste() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).deleteMensajeById(99L);
        mvc.perform(delete("/api/v1/mensajes-chat/99"))
                .andExpect(status().isNotFound());
    }
}
