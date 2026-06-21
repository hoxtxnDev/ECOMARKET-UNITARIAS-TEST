package com.ecomarket.analiticaservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ecomarket.analiticaservice.dto.AccionLogDTO;
import com.ecomarket.analiticaservice.exception.GlobalExceptionHandler;
import com.ecomarket.analiticaservice.service.AnaliticaService;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    private MockMvc mockMvc;

    @Mock private AnaliticaService analiticaService;

    @InjectMocks private LogController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

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
