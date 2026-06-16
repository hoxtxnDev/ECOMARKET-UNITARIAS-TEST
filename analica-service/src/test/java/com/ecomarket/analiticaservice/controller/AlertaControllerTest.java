package com.ecomarket.analiticaservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.analiticaservice.dto.AlertaRequestDTO;
import com.ecomarket.analiticaservice.model.entity.AlertaSistema;
import com.ecomarket.analiticaservice.model.reference.NivelAlerta;
import com.ecomarket.analiticaservice.service.AnaliticaService;

@WebMvcTest(AlertaController.class)
class AlertaControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AnaliticaService analiticaService;

    @Test
    void listarReturnsOk() throws Exception {
        when(analiticaService.listarAlertas()).thenReturn(List.of(new AlertaSistema()));

        mockMvc.perform(get("/api/v1/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void obtenerReturnsOk() throws Exception {
        AlertaSistema alerta = new AlertaSistema(1L, new NivelAlerta(), "Msg", "Mod", null, false);
        when(analiticaService.obtenerAlerta(1L)).thenReturn(alerta);

        mockMvc.perform(get("/api/v1/alertas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Msg"));
    }

    @Test
    void listarPorEstadoReturnsOk() throws Exception {
        when(analiticaService.listarAlertasPorEstado(true)).thenReturn(List.of(new AlertaSistema()));

        mockMvc.perform(get("/api/v1/alertas/estado?resuelta=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void crearReturnsCreated() throws Exception {
        AlertaSistema alerta = new AlertaSistema(1L, new NivelAlerta(), "Msg", "Mod", null, false);
        when(analiticaService.crearAlerta(any(AlertaRequestDTO.class))).thenReturn(alerta);

        mockMvc.perform(post("/api/v1/alertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nivelAlertaId":1,"mensaje":"Msg","moduloOrigen":"Mod"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Msg"));
    }

    @Test
    void resolverReturnsOk() throws Exception {
        AlertaSistema alerta = new AlertaSistema(1L, new NivelAlerta(), "Msg", "Mod", null, true);
        when(analiticaService.resolverAlerta(1L)).thenReturn(alerta);

        mockMvc.perform(patch("/api/v1/alertas/1/resolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resuelta").value(true));
    }
}
