package com.ecomarket.catalogoinventarioservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.catalogoinventarioservice.model.CategoriaProducto;
import com.ecomarket.catalogoinventarioservice.model.EstadoDisponibilidad;
import com.ecomarket.catalogoinventarioservice.model.EspecificacionTecnica;
import com.ecomarket.catalogoinventarioservice.model.Producto;
import com.ecomarket.catalogoinventarioservice.service.CatalogoService;

@WebMvcTest(CatalogoAdminController.class)
class CatalogoAdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CatalogoService catalogoService;

    @Test
    void listarCategoriasReturnsOk() throws Exception {
        when(catalogoService.listarCategorias()).thenReturn(List.of(new CategoriaProducto(1L, "Electronica")));

        mockMvc.perform(get("/api/catalogo-admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Electronica"));
    }

    @Test
    void obtenerCategoriaReturnsOk() throws Exception {
        when(catalogoService.obtenerCategoria(1L)).thenReturn(new CategoriaProducto(1L, "Electronica"));

        mockMvc.perform(get("/api/catalogo-admin/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Electronica"));
    }

    @Test
    void crearCategoriaReturnsCreated() throws Exception {
        when(catalogoService.crearCategoria(new CategoriaProducto(null, "Hogar")))
                .thenReturn(new CategoriaProducto(2L, "Hogar"));

        mockMvc.perform(post("/api/catalogo-admin/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Hogar\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hogar"));
    }

    @Test
    void editarCategoriaReturnsUpdated() throws Exception {
        when(catalogoService.editarCategoria(1L, new CategoriaProducto(null, "Hogar")))
                .thenReturn(new CategoriaProducto(1L, "Hogar"));

        mockMvc.perform(put("/api/catalogo-admin/categoria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Hogar\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hogar"));
    }

    @Test
    void eliminarCategoriaReturnsTrue() throws Exception {
        when(catalogoService.eliminarCategoria(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/catalogo-admin/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void listarEstadosReturnsOk() throws Exception {
        when(catalogoService.listarEstados()).thenReturn(List.of(new EstadoDisponibilidad(1L, "Disponible")));

        mockMvc.perform(get("/api/catalogo-admin/estados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Disponible"));
    }

    @Test
    void crearEstadoReturnsCreated() throws Exception {
        when(catalogoService.crearEstado(new EstadoDisponibilidad(null, "Agotado")))
                .thenReturn(new EstadoDisponibilidad(1L, "Agotado"));

        mockMvc.perform(post("/api/catalogo-admin/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Agotado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agotado"));
    }

    @Test
    void listarEspecificacionesReturnsOk() throws Exception {
        when(catalogoService.listarEspecificaciones()).thenReturn(List.of());

        mockMvc.perform(get("/api/catalogo-admin/especificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listarEspecificacionesPorProductoReturnsOk() throws Exception {
        when(catalogoService.listarEspecificacionesPorProducto(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/catalogo-admin/especificaciones/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerEstadoReturnsOk() throws Exception {
        when(catalogoService.obtenerEstado(1L)).thenReturn(new EstadoDisponibilidad(1L, "Disponible"));

        mockMvc.perform(get("/api/catalogo-admin/estado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Disponible"));
    }

    @Test
    void editarEstadoReturnsUpdated() throws Exception {
        when(catalogoService.editarEstado(1L, new EstadoDisponibilidad(null, "Agotado")))
                .thenReturn(new EstadoDisponibilidad(1L, "Agotado"));

        mockMvc.perform(put("/api/catalogo-admin/estado/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Agotado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agotado"));
    }

    @Test
    void eliminarEstadoReturnsTrue() throws Exception {
        when(catalogoService.eliminarEstado(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/catalogo-admin/estado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void obtenerEspecificacionReturnsOk() throws Exception {
        Producto p = new Producto();
        p.setId(1L);
        when(catalogoService.obtenerEspecificacion(1L))
                .thenReturn(new EspecificacionTecnica(1L, "RAM", "16GB", p));

        mockMvc.perform(get("/api/catalogo-admin/especificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("RAM"))
                .andExpect(jsonPath("$.valor").value("16GB"));
    }

    @Test
    void crearEspecificacionReturnsOk() throws Exception {
        when(catalogoService.crearEspecificacion(any(EspecificacionTecnica.class)))
                .thenReturn(new EspecificacionTecnica(1L, "RAM", "16GB", null));

        mockMvc.perform(post("/api/catalogo-admin/especificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clave\":\"RAM\",\"valor\":\"16GB\",\"producto\":{\"id\":1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("RAM"));
    }

    @Test
    void editarEspecificacionReturnsOk() throws Exception {
        when(catalogoService.editarEspecificacion(any(Long.class), any(EspecificacionTecnica.class)))
                .thenReturn(new EspecificacionTecnica(1L, "CPU", "i7", null));

        mockMvc.perform(put("/api/catalogo-admin/especificaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clave\":\"CPU\",\"valor\":\"i7\",\"producto\":{\"id\":1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("CPU"));
    }

    @Test
    void eliminarEspecificacionReturnsTrue() throws Exception {
        when(catalogoService.eliminarEspecificacion(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/catalogo-admin/especificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

}
