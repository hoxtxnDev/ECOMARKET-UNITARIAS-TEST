package com.ecomarket.carritocompraservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.carritocompraservice.dto.CompraRequestDTO;
import com.ecomarket.carritocompraservice.dto.CompraResultDTO;
import com.ecomarket.carritocompraservice.service.CompraOrchestratorService;

@WebMvcTest(CompraController.class)
class CompraControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CompraOrchestratorService compraOrchestratorService;

    @Test
    void finalizarCompraReturnsOk() throws Exception {
        CompraResultDTO result = new CompraResultDTO();
        result.setEstado("COMPLETADO");
        when(compraOrchestratorService.ejecutarCompra(any(CompraRequestDTO.class))).thenReturn(result);

        mockMvc.perform(post("/api/compra/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"metodoEnvioId":1,"direccionId":1,"metodoPagoId":1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }
}
