package com.horacio.ecomarket.usuarios.controller;

import tools.jackson.databind.ObjectMapper;
import com.horacio.ecomarket.usuarios.dto.ConfigurarPermisosDTO;
import com.horacio.ecomarket.usuarios.dto.ModificarUsuarioDTO;
import com.horacio.ecomarket.usuarios.dto.RegistroUsuarioDTO;
import com.horacio.ecomarket.usuarios.model.EstadoPerfil;
import com.horacio.ecomarket.usuarios.model.PerfilUsuario;
import com.horacio.ecomarket.usuarios.model.Permiso;
import com.horacio.ecomarket.usuarios.model.Rol;
import com.horacio.ecomarket.usuarios.repository.EstadoPerfilRepository;
import com.horacio.ecomarket.usuarios.repository.PermisoRepository;
import com.horacio.ecomarket.usuarios.repository.RolRepository;
import com.horacio.ecomarket.usuarios.service.RegistroUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RegistroUsuarioService service;
    @MockitoBean
    private RolRepository rolRepository;
    @MockitoBean
    private PermisoRepository permisoRepository;
    @MockitoBean
    private EstadoPerfilRepository estadoPerfilRepository;

    private PerfilUsuario perfilBase;
    private Rol rolMock;
    private EstadoPerfil estadoMock;

    @BeforeEach
    void setUp() {
        rolMock = Rol.builder().id(1L).nombre("CLIENTE").build();

        estadoMock = new EstadoPerfil();
        estadoMock.setId(1L);
        estadoMock.setNombre("ACTIVO");

        perfilBase = PerfilUsuario.builder()
                .id(1L)
                .nombre("Horacio")
                .correo("h@eco.cl")
                .telefono("123456")
                .rol(rolMock)
                .estadoPerfil(estadoMock)
                .build();
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("404 NOT FOUND cuando el RolId no existe")
        void registrarRolNoExiste() throws Exception {
            RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
            dto.setNombre("Test");
            dto.setCorreo("test@eco.cl");
            dto.setContrasenaInicial("pass1234");
            dto.setRolId(99L);

            when(rolRepository.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("201 CREATED al registrar usuario con datos válidos")
        void registrarExitoso() throws Exception {
            RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
            dto.setNombre("Horacio");
            dto.setCorreo("h@eco.cl");
            dto.setContrasenaInicial("pass1234");
            dto.setRolId(1L);

            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(service.registrarCuenta(any(PerfilUsuario.class), eq("pass1234"))).thenReturn(perfilBase);

            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.correo").value("h@eco.cl"));
        }

        @Test
        @DisplayName("201 CREATED al registrar sin rolId ni estadoPerfilId")
        void registrarSinRolNiEstado() throws Exception {
            RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
            dto.setNombre("Horacio");
            dto.setCorreo("h@eco.cl");
            dto.setContrasenaInicial("pass1234");

            when(service.registrarCuenta(any(PerfilUsuario.class), eq("pass1234"))).thenReturn(perfilBase);

            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("201 CREATED al registrar con rolId y estadoPerfilId")
        void registrarConEstado() throws Exception {
            RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
            dto.setNombre("Horacio");
            dto.setCorreo("h@eco.cl");
            dto.setContrasenaInicial("pass1234");
            dto.setRolId(1L);
            dto.setEstadoPerfilId(1L);

            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(estadoPerfilRepository.findById(1L)).thenReturn(Optional.of(estadoMock));
            when(service.registrarCuenta(any(PerfilUsuario.class), eq("pass1234"))).thenReturn(perfilBase);

            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.correo").value("h@eco.cl"));
        }

        @Test
        @DisplayName("404 NOT FOUND cuando el EstadoPerfilId no existe en BD")
        void registrarEstadoNoExiste() throws Exception {
            RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
            dto.setNombre("Test");
            dto.setCorreo("test@eco.cl");
            dto.setContrasenaInicial("pass1234");
            dto.setRolId(1L);
            dto.setEstadoPerfilId(99L);

            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(estadoPerfilRepository.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("modificar")
    class Modificar {
        @Test
        @DisplayName("404 NOT FOUND cuando el RolId no existe en modificar")
        void modificarRolNoExiste() throws Exception {
            ModificarUsuarioDTO dto = new ModificarUsuarioDTO();
            dto.setNombre("Test");
            dto.setCorreo("test@eco.cl");
            dto.setRolId(99L);

            when(rolRepository.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/usuarios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("404 NOT FOUND cuando el EstadoPerfilId no existe en BD")
        void modificarEstadoNoExiste() throws Exception {
            ModificarUsuarioDTO dto = new ModificarUsuarioDTO();
            dto.setNombre("Test");
            dto.setCorreo("test@eco.cl");
            dto.setRolId(1L);
            dto.setEstadoPerfilId(99L);

            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(estadoPerfilRepository.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/usuarios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 OK al modificar usuario con datos válidos")
        void modificarExitoso() throws Exception {
            ModificarUsuarioDTO dto = new ModificarUsuarioDTO();
            dto.setNombre("Horacio Modificado");
            dto.setCorreo("h@eco.cl");
            dto.setRolId(1L);

            PerfilUsuario perfilModificado = PerfilUsuario.builder()
                    .id(1L)
                    .nombre("Horacio Modificado")
                    .correo("h@eco.cl")
                    .telefono("123456")
                    .rol(rolMock)
                    .build();

            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(service.modificarDatosUsuario(eq(1L), any(PerfilUsuario.class))).thenReturn(perfilModificado);

            mockMvc.perform(put("/api/usuarios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Horacio Modificado"));
        }

        @Test
        @DisplayName("200 OK al modificar con rolId Y estadoPerfilId")
        void modificarConRolYEstado() throws Exception {
            ModificarUsuarioDTO dto = new ModificarUsuarioDTO();
            dto.setNombre("Con Estado");
            dto.setCorreo("conestado@eco.cl");
            dto.setRolId(1L);
            dto.setEstadoPerfilId(1L);

            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(estadoPerfilRepository.findById(1L)).thenReturn(Optional.of(estadoMock));
            when(service.modificarDatosUsuario(eq(1L), any(PerfilUsuario.class))).thenReturn(perfilBase);

            mockMvc.perform(put("/api/usuarios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("200 OK al modificar sin rolId ni estadoPerfilId")
        void modificarSinRolNiEstado() throws Exception {
            ModificarUsuarioDTO dto = new ModificarUsuarioDTO();
            dto.setNombre("Sin Rol");
            dto.setCorreo("sinrol@eco.cl");

            when(service.modificarDatosUsuario(eq(1L), any(PerfilUsuario.class))).thenReturn(perfilBase);

            mockMvc.perform(put("/api/usuarios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Consultas GET")
    class ConsultasGet {

        @Test
        @DisplayName("GET /api/usuarios — 200 OK retorna lista")
        void listarTodos() throws Exception {
            when(service.listarUsuarios()).thenReturn(List.of(perfilBase));

            mockMvc.perform(get("/api/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].correo").value("h@eco.cl"));
        }

        @Test
        @DisplayName("GET /api/usuarios/{id} — 200 OK")
        void buscarPorId() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(perfilBase);

            mockMvc.perform(get("/api/usuarios/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.correo").value("h@eco.cl"));
        }

        @Test
        @DisplayName("GET /api/usuarios/rol/{rolId} — 404 NOT FOUND cuando el rol no existe")
        void listarPorRolNoExiste() throws Exception {
            when(rolRepository.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/usuarios/rol/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/usuarios/rol/{rolId} — 200 OK")
        void listarPorRol() throws Exception {
            when(rolRepository.findById(1L)).thenReturn(Optional.of(rolMock));
            when(service.listarPorRol(rolMock)).thenReturn(List.of(perfilBase));

            mockMvc.perform(get("/api/usuarios/rol/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].correo").value("h@eco.cl"));
        }

        @Test
        @DisplayName("GET /api/usuarios/correo/{correo} — 200 OK")
        void buscarPorCorreo() throws Exception {
            when(service.buscarPorCorreo("h@eco.cl")).thenReturn(perfilBase);

            mockMvc.perform(get("/api/usuarios/correo/h@eco.cl"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correo").value("h@eco.cl"));
        }
    }

    @Nested
    @DisplayName("configurarPermisos")
    class ConfigurarPermisos {

        @Test
        @DisplayName("200 OK al asignar permisos existentes")
        void configurarPermisosExitoso() throws Exception {
            ConfigurarPermisosDTO dto = new ConfigurarPermisosDTO();
            dto.setPermisoIds(List.of(10L, 20L));

            Permiso p1 = Permiso.builder().id(10L).build();
            Permiso p2 = Permiso.builder().id(20L).build();

            when(permisoRepository.findById(10L)).thenReturn(Optional.of(p1));
            when(permisoRepository.findById(20L)).thenReturn(Optional.of(p2));
            when(service.configurarPermisos(eq(1L), anyList())).thenReturn(true);

            mockMvc.perform(put("/api/usuarios/1/permisos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("404 NOT FOUND si un permiso no existe")
        void configurarPermisosFallaSiNoExiste() throws Exception {
            ConfigurarPermisosDTO dto = new ConfigurarPermisosDTO();
            dto.setPermisoIds(List.of(99L));

            when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/usuarios/1/permisos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("200 OK al eliminar usuario")
        void eliminarExitoso() throws Exception {
            when(service.eliminarUsuario(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/usuarios/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            verify(service).eliminarUsuario(1L);
        }
    }

}
