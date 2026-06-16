package com.ecomarket.catalogoinventarioservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.catalogoinventarioservice.model.InventarioStock;
import com.ecomarket.catalogoinventarioservice.model.Producto;
import com.ecomarket.catalogoinventarioservice.service.InventarioService;

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private InventarioService inventarioService;

    private InventarioStock stock() {
        InventarioStock s = new InventarioStock();
        s.setId(1L);
        s.setProducto(new Producto());
        s.getProducto().setId(1L);
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
        when(inventarioService.verificarDisponibilidad(1L, 10)).thenReturn(true);

        mockMvc.perform(get("/api/inventario/disponibilidad/1").param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void verificarDisponibilidadReturnsFalse() throws Exception {
        when(inventarioService.verificarDisponibilidad(1L, 999)).thenReturn(false);

        mockMvc.perform(get("/api/inventario/disponibilidad/1").param("cantidad", "999"))
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
    void reservarStockReturnsTrue() throws Exception {
        when(inventarioService.reservarStock(1L, 5)).thenReturn(true);

        mockMvc.perform(post("/api/inventario/reservar/1").param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void liberarStockReturnsTrue() throws Exception {
        when(inventarioService.liberarStock(1L, 5)).thenReturn(true);

        mockMvc.perform(post("/api/inventario/liberar/1").param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void ajustarStockUpdatesAndReturns() throws Exception {
        when(inventarioService.ajustarStock(1L, 10L, 100)).thenReturn(stock());

        mockMvc.perform(put("/api/inventario/ajustar/1/sucursal/10").param("nuevaCantidad", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(50));
    }

}
