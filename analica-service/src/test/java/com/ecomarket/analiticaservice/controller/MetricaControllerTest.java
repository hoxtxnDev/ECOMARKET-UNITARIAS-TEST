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

import com.ecomarket.analiticaservice.dto.MetricaRequestDTO;
import com.ecomarket.analiticaservice.model.entity.MetricaDashboard;
import com.ecomarket.analiticaservice.service.AnaliticaService;

@WebMvcTest(MetricaController.class)
class MetricaControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AnaliticaService analiticaService;

    @Test
    void listarReturnsOk() throws Exception {
        when(analiticaService.listarMetricas()).thenReturn(List.of(new MetricaDashboard()));

        mockMvc.perform(get("/api/v1/metricas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void obtenerReturnsOk() throws Exception {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 100.0, "texto", null);
        when(analiticaService.obtenerMetricaPorId(1L)).thenReturn(m);

        mockMvc.perform(get("/api/v1/metricas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claveMetrica").value("ventas"));
    }

    @Test
    void obtenerPorClaveReturnsOk() throws Exception {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 100.0, "texto", null);
        when(analiticaService.obtenerMetricaPorClave("ventas")).thenReturn(m);

        mockMvc.perform(get("/api/v1/metricas/clave/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claveMetrica").value("ventas"));
    }

    @Test
    void crearReturnsCreated() throws Exception {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 100.0, "texto", null);
        when(analiticaService.crearMetrica(any(MetricaRequestDTO.class))).thenReturn(m);

        mockMvc.perform(post("/api/v1/metricas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"claveMetrica":"ventas","valorNumerico":100.0,"valorTexto":"texto"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claveMetrica").value("ventas"));
    }

    @Test
    void actualizarReturnsOk() throws Exception {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 200.0, "new", null);
        when(analiticaService.actualizarMetrica(anyLong(), any(MetricaRequestDTO.class))).thenReturn(m);

        mockMvc.perform(put("/api/v1/metricas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"claveMetrica":"ventas","valorNumerico":200.0,"valorTexto":"new"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorNumerico").value(200.0));
    }
}
