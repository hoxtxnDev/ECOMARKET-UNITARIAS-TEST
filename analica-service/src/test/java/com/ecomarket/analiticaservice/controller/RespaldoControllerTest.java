package com.ecomarket.analiticaservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ecomarket.analiticaservice.dto.RespaldoRequestDTO;
import com.ecomarket.analiticaservice.exception.GlobalExceptionHandler;
import com.ecomarket.analiticaservice.model.entity.RespaldoBaseDatos;
import com.ecomarket.analiticaservice.model.reference.EstadoRespaldo;
import com.ecomarket.analiticaservice.service.AnaliticaService;

@ExtendWith(MockitoExtension.class)
class RespaldoControllerTest {

    private MockMvc mockMvc;

    @Mock private AnaliticaService analiticaService;

    @InjectMocks private RespaldoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listarReturnsOk() throws Exception {
        when(analiticaService.listarRespaldos()).thenReturn(List.of(new RespaldoBaseDatos()));

        mockMvc.perform(get("/api/v1/respaldos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void obtenerReturnsOk() throws Exception {
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, null, 10.0, new EstadoRespaldo(), "/ruta");
        when(analiticaService.obtenerRespaldo(1L)).thenReturn(r);

        mockMvc.perform(get("/api/v1/respaldos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tamanoMegabytes").value(10.0));
    }

    @Test
    void ejecutarReturnsCreated() throws Exception {
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, null, 10.0, new EstadoRespaldo(), "/ruta");
        when(analiticaService.ejecutarRespaldo(any(RespaldoRequestDTO.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/respaldos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"estadoRespaldoId":1,"tamanoMegabytes":10.0,"rutaAlmacenamiento":"/ruta"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tamanoMegabytes").value(10.0));
    }
}
