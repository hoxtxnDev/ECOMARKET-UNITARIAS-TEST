package com.ecomarket.catalogoinventarioservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.catalogoinventarioservice.model.CategoriaProducto;
import com.ecomarket.catalogoinventarioservice.model.EstadoDisponibilidad;
import com.ecomarket.catalogoinventarioservice.model.Producto;
import com.ecomarket.catalogoinventarioservice.service.CatalogoService;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CatalogoService catalogoService;

    private Producto producto() {
        return new Producto(1L, "SKU-001", "Laptop", "Laptop Gamer", 1500.0,
                new CategoriaProducto(1L, "Electronica"), new EstadoDisponibilidad(1L, "Disponible"),
                "img.jpg", LocalDateTime.now());
    }

    @Test
    void listarProductosReturnsOk() throws Exception {
        when(catalogoService.listarTodos()).thenReturn(List.of(producto()));

        mockMvc.perform(get("/api/catalogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sku").value("SKU-001"));
    }

    @Test
    void navegarCatalogoReturnsProductsByCategory() throws Exception {
        when(catalogoService.navegarCatalogo(1L)).thenReturn(List.of(producto()));

        mockMvc.perform(get("/api/catalogo/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));
    }

    @Test
    void buscarPorNombreReturnsFiltered() throws Exception {
        when(catalogoService.buscarCoincidenciaPorNombre("laptop")).thenReturn(List.of(producto()));

        mockMvc.perform(get("/api/catalogo/buscar").param("nombre", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("SKU-001"));
    }

    @Test
    void obtenerProductoReturnsProduct() throws Exception {
        when(catalogoService.consultarDetalles(1L)).thenReturn(producto());

        mockMvc.perform(get("/api/catalogo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void agregarProductoCreatesAndReturns() throws Exception {
        when(catalogoService.agregarProducto(any(Producto.class))).thenReturn(producto());

        mockMvc.perform(post("/api/catalogo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"SKU-001","nombre":"Laptop","descripcion":"Laptop Gamer","precioBase":1500.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-001"));
    }

    @Test
    void editarProductoUpdatesAndReturns() throws Exception {
        when(catalogoService.editarProducto(any(Long.class), any(Producto.class))).thenReturn(producto());

        mockMvc.perform(put("/api/catalogo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"SKU-001","nombre":"Laptop","precioBase":1500.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-001"));
    }

    @Test
    void eliminarProductoReturnsTrue() throws Exception {
        when(catalogoService.eliminarProducto(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/catalogo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

}
