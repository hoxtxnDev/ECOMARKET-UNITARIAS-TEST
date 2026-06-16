package com.ecomarket.analiticaservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.analiticaservice.dto.AccionLogDTO;
import com.ecomarket.analiticaservice.service.AnaliticaService;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AnaliticaService analiticaService;

    @Test
    void registrarAccionReturnsOk() throws Exception {
        doNothing().when(analiticaService).registrarAccion(any(AccionLogDTO.class));

        mockMvc.perform(post("/api/analitica/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"microservicio":"test","accion":"CREAR","usuarioId":1}
                                """))
                .andExpect(status().isOk());
    }
}
