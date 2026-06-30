package com.ecomarket.catalogoinventarioservice.controller;

import static org.mockito.Mockito.doNothing;
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

import com.ecomarket.catalogoinventarioservice.dto.MensajeDTO;
import com.ecomarket.catalogoinventarioservice.model.InventarioStock;
import com.ecomarket.catalogoinventarioservice.model.StockGlobal;
import com.ecomarket.catalogoinventarioservice.service.InventarioService;

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private InventarioService inventarioService;

    private InventarioStock stock() {
        InventarioStock s = new InventarioStock();
        s.setId(1L);
        s.setProductoId(1L);
        s.setSucursalId(10L);
        s.setCantidadDisponible(50);
        s.setCantidadReservada(10);
        return s;
    }

    @Test
    void listarInventarioReturnsOk() throws Exception {
        when(inventarioService.listarTodos()).thenReturn(List.of(stock()));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void verificarDisponibilidadReturnsTrue() throws Exception {
        when(inventarioService.verificarDisponibilidad(1L, 10L, 10)).thenReturn(true);

        mockMvc.perform(get("/api/inventario/disponibilidad/1/10/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void verificarDisponibilidadReturnsFalse() throws Exception {
        when(inventarioService.verificarDisponibilidad(1L, 10L, 999)).thenReturn(false);

        mockMvc.perform(get("/api/inventario/disponibilidad/1/10/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void consultarGlobalReturnsStockList() throws Exception {
        when(inventarioService.consultarInventarioGlobal(1L)).thenReturn(List.of(stock()));

        mockMvc.perform(get("/api/inventario/global/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidadDisponible").value(50));
    }

    @Test
    void consultarPorSucursalReturnsFiltered() throws Exception {
        when(inventarioService.consultarInventarioPorSucursal(10L, 1L)).thenReturn(List.of(stock()));

        mockMvc.perform(get("/api/inventario/sucursal/10/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sucursalId").value(10));
    }

    @Test
    void reservarStockReturnsOk() throws Exception {
        doNothing().when(inventarioService).reservarStock(1L, 10L, 5);

        mockMvc.perform(post("/api/inventario/reservar/1/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    void liberarStockReturnsOk() throws Exception {
        doNothing().when(inventarioService).liberarStock(1L, 10L, 5);

        mockMvc.perform(post("/api/inventario/liberar/1/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    void ajustarStockReturnsOk() throws Exception {
        doNothing().when(inventarioService).ajustarStock(1L, 10L, 100);

        mockMvc.perform(put("/api/inventario/ajustar/1/sucursal/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":100}"))
                .andExpect(status().isOk());
    }

    @Test
    void consultarStockGlobalReturnsOk() throws Exception {
        StockGlobal sg = new StockGlobal();
        sg.setId(1L);
        sg.setProductoId(1L);
        sg.setCantidadDisponible(200);
        when(inventarioService.consultarStockGlobal(1L)).thenReturn(sg);

        mockMvc.perform(get("/api/inventario/stock-global/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidadDisponible").value(200));
    }

    @Test
    void ingresarStockGlobalReturnsOk() throws Exception {
        StockGlobal sg = new StockGlobal();
        sg.setId(1L);
        sg.setProductoId(1L);
        sg.setCantidadDisponible(150);
        when(inventarioService.ingresarStockGlobal(1L, 50)).thenReturn(sg);

        mockMvc.perform(post("/api/inventario/ingresar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":1,\"cantidad\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(150));
    }

    @Test
    void transferirStockDesdeGlobalReturnsOk() throws Exception {
        MensajeDTO msg = new MensajeDTO("Transferencia completada");
        when(inventarioService.transferirStockDesdeGlobal(1L, 10L, 50)).thenReturn(msg);

        mockMvc.perform(post("/api/inventario/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":1,\"sucursalId\":10,\"cantidad\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Transferencia completada"));
    }

}
