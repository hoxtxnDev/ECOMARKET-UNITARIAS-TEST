package com.ecomarket.analiticaservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.analiticaservice.dto.ReporteFechaRequestDTO;
import com.ecomarket.analiticaservice.dto.ReporteRequestDTO;
import com.ecomarket.analiticaservice.model.entity.Reporte;
import com.ecomarket.analiticaservice.service.AnaliticaService;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AnaliticaService analiticaService;

    @Test
    void listarReturnsOk() throws Exception {
        when(analiticaService.listarReportes()).thenReturn(List.of(new Reporte()));

        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void obtenerReturnsOk() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.obtenerReporte(1L)).thenReturn(r);

        mockMvc.perform(get("/api/v1/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listarPorSolicitanteReturnsOk() throws Exception {
        when(analiticaService.listarReportesPorSolicitante(1L)).thenReturn(List.of(new Reporte()));

        mockMvc.perform(get("/api/v1/reportes/solicitante/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void listarPorRangoReturnsOk() throws Exception {
        when(analiticaService.listarReportesPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Reporte()));

        mockMvc.perform(get("/api/v1/reportes/rango")
                        .param("inicio", "2026-01-01T00:00:00")
                        .param("fin", "2026-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void generarReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporte(any(ReporteRequestDTO.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"solicitanteId":1,"tipoReporteId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarPorRangoReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReportePorRango(any(ReporteFechaRequestDTO.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/rango")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"solicitanteId":1,"tipoReporteId":1,"fechaInicio":"2026-01-01T00:00:00","fechaFin":"2026-12-31T23:59:59"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReporteUsuariosReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporteUsuarios(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/usuarios/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReportePedidosReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReportePedidos(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/pedidos/1")
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReporteInventarioReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporteInventario(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/inventario/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReportePagosReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReportePagos(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/pagos/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReporteCarritoReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporteCarrito(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/carrito/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReporteSoporteReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporteSoporte(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/soporte/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReporteEnviosReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporteEnvios(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/envios/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void generarReporteCompletoReturnsCreated() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        when(analiticaService.generarReporteCompleto(1L)).thenReturn(r);

        mockMvc.perform(post("/api/v1/reportes/completo/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
