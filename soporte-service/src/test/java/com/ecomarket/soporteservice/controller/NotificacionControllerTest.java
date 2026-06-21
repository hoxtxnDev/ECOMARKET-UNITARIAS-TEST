package com.ecomarket.soporteservice.controller;

import com.ecomarket.soporteservice.exception.GlobalExceptionHandler;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.model.entity.Notificacion;
import com.ecomarket.soporteservice.model.reference.CanalNotificacion;
import com.ecomarket.soporteservice.service.CanalNotificacionService;
import com.ecomarket.soporteservice.service.NotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionController")
class NotificacionControllerTest {

    MockMvc mvc;
    @SuppressWarnings("unused")
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock NotificacionService service;
    @Mock CanalNotificacionService canalNotificacionService;
    @Mock RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        mvc = standaloneSetup(new NotificacionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Notificacion notif(Long id) {
        return Notificacion.builder()
                .id(id).destinatarioId(5L)
                .canal(new CanalNotificacion(1L, "EMAIL"))
                .titulo("Pedido listo").cuerpo("Tu pedido está listo")
                .fechaEnvioNotificacion(LocalDateTime.now())
                .enviadaConExito(true)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/notificaciones sin param → 200 lista completa")
    void getAllSinParam() throws Exception {
        when(service.readAllNotificacion()).thenReturn(List.of(notif(1L), notif(2L)));
        mvc.perform(get("/api/v1/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/notificaciones?destinatarioId=5 → 200 filtrado")
    void getAllConDestinatario() throws Exception {
        when(service.readNotificacionesByDestinatarioId(5L)).thenReturn(List.of(notif(1L)));
        mvc.perform(get("/api/v1/notificaciones").param("destinatarioId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/notificaciones/{id} → 200 por id")
    void getById() throws Exception {
        when(service.findNotificacionById(1L)).thenReturn(notif(1L));
        mvc.perform(get("/api/v1/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /{id} inexistente → 404")
    void getByIdNoExiste() throws Exception {
        when(service.findNotificacionById(99L)).thenThrow(new NoExisteEnBdException("99 no existe."));
        mvc.perform(get("/api/v1/notificaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/notificaciones/1 → 200")
    void deleteExistente() throws Exception {
        doNothing().when(service).deleteNotificacionById(1L);
        mvc.perform(delete("/api/v1/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    @DisplayName("DELETE inexistente → 404")
    void deleteInexistente() throws Exception {
        doThrow(new NoExisteEnBdException("99 no existe.")).when(service).deleteNotificacionById(99L);
        mvc.perform(delete("/api/v1/notificaciones/99"))
                .andExpect(status().isNotFound());
    }
}
