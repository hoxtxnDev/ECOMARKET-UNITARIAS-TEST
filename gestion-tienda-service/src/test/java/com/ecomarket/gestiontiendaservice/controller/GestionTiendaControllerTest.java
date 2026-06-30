package com.ecomarket.gestiontiendaservice.controller;

import com.ecomarket.gestiontiendaservice.dto.EstadoRequestDTO;
import com.ecomarket.gestiontiendaservice.dto.GerenteRequestDTO;
import com.ecomarket.gestiontiendaservice.dto.SucursalRequestDTO;
import com.ecomarket.gestiontiendaservice.model.*;
import com.ecomarket.gestiontiendaservice.service.GestionTiendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionTiendaControllerTest {

    @Mock
    GestionTiendaService gestionTiendaService;

    GestionTiendaController controller;

    @BeforeEach
    void setup() {
        controller = new GestionTiendaController(gestionTiendaService);
    }

    @Test
    @DisplayName("registrarSucursal")
    void registrarSucursal() {
        Sucursal s = new Sucursal();
        s.setId(1L);
        when(gestionTiendaService.registrarSucursal(any(SucursalRequestDTO.class))).thenReturn(s);

        SucursalRequestDTO dto = new SucursalRequestDTO();
        dto.setNombre("Centro");
        dto.setDireccion("Av 1");
        dto.setTelefono("412345");
        dto.setGerenteCargoId(10L);

        ResponseEntity<Sucursal> res = controller.registrarSucursal(dto);

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerSucursal")
    void obtenerSucursal() {
        Sucursal s = new Sucursal();
        s.setId(1L);
        when(gestionTiendaService.obtenerDatosSucursal(1L)).thenReturn(s);

        ResponseEntity<Sucursal> res = controller.obtenerSucursal(1L);

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("listarSucursalesActivas")
    void listarSucursalesActivas() {
        when(gestionTiendaService.listarSucursalesActivas()).thenReturn(List.of(new Sucursal()));

        ResponseEntity<List<Sucursal>> res = controller.listarSucursalesActivas();

        assertThat(res.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("asignarGerente")
    void asignarGerente() {
        Sucursal s = new Sucursal();
        s.setId(1L);
        s.setGerenteCargoId(20L);
        when(gestionTiendaService.asignarGerente(1L, 20L)).thenReturn(s);

        GerenteRequestDTO dto = new GerenteRequestDTO();
        dto.setGerenteCargoId(20L);

        ResponseEntity<Sucursal> res = controller.asignarGerente(1L, dto);

        assertThat(res.getBody().getGerenteCargoId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("configurarPermisoPOS")
    void configurarPermisoPOS() {
        PermisoPOS p = new PermisoPOS();
        p.setId(1L);
        when(gestionTiendaService.configurarPermisoPOS(any())).thenReturn(p);

        ResponseEntity<PermisoPOS> res = controller.configurarPermisoPOS(new PermisoPOS());

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("asignarTarea")
    void asignarTarea() {
        TareaPersonal t = new TareaPersonal();
        t.setId(1L);
        when(gestionTiendaService.asignarTareaPersonal(any(TareaPersonal.class))).thenReturn(t);

        TareaPersonal tarea = new TareaPersonal();
        tarea.setEmpleadoId(5L);
        tarea.setSucursalId(1L);
        tarea.setTitulo("Tit");
        tarea.setDescripcion("Desc");

        ResponseEntity<TareaPersonal> res = controller.asignarTarea(tarea);

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("actualizarEstadoTarea")
    void actualizarEstadoTarea() {
        TareaPersonal t = new TareaPersonal();
        t.setId(1L);
        when(gestionTiendaService.actualizarEstadoTarea(1L, 2L)).thenReturn(t);

        EstadoRequestDTO dto = new EstadoRequestDTO();
        dto.setEstadoId(2L);

        ResponseEntity<TareaPersonal> res = controller.actualizarEstadoTarea(1L, dto);

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("listarEstadosTarea")
    void listarEstadosTarea() {
        when(gestionTiendaService.listarEstadosTarea()).thenReturn(List.of(new EstadoTareaPersonal()));

        ResponseEntity<List<EstadoTareaPersonal>> res = controller.listarEstadosTarea();

        assertThat(res.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerEstadoTarea")
    void obtenerEstadoTarea() {
        EstadoTareaPersonal e = new EstadoTareaPersonal();
        e.setId(1L);
        when(gestionTiendaService.obtenerEstadoTarea(1L)).thenReturn(e);

        ResponseEntity<EstadoTareaPersonal> res = controller.obtenerEstadoTarea(1L);

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("crearEstadoTarea")
    void crearEstadoTarea() {
        EstadoTareaPersonal e = new EstadoTareaPersonal();
        e.setId(1L);
        when(gestionTiendaService.crearEstadoTarea(any())).thenReturn(e);

        ResponseEntity<EstadoTareaPersonal> res = controller.crearEstadoTarea(new EstadoTareaPersonal());

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("editarEstadoTarea")
    void editarEstadoTarea() {
        EstadoTareaPersonal e = new EstadoTareaPersonal();
        e.setId(1L);
        when(gestionTiendaService.editarEstadoTarea(eq(1L), any())).thenReturn(e);

        ResponseEntity<EstadoTareaPersonal> res = controller.editarEstadoTarea(1L, new EstadoTareaPersonal());

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("eliminarEstadoTarea")
    void eliminarEstadoTarea() {
        when(gestionTiendaService.eliminarEstadoTarea(1L)).thenReturn(true);

        ResponseEntity<Boolean> res = controller.eliminarEstadoTarea(1L);

        assertThat(res.getBody()).isTrue();
    }

    @Test
    @DisplayName("establecerReglamento")
    void establecerReglamento() {
        ReglamentoInterno r = new ReglamentoInterno();
        r.setId(1L);
        when(gestionTiendaService.establecerReglamento(any())).thenReturn(r);

        ResponseEntity<ReglamentoInterno> res = controller.establecerReglamento(new ReglamentoInterno());

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("administrarHorario")
    void administrarHorario() {
        when(gestionTiendaService.administrarHorario(anyLong(), anyList())).thenReturn(true);

        ResponseEntity<Boolean> res = controller.administrarHorario(1L, List.of());

        assertThat(res.getBody()).isTrue();
    }

    @Test
    @DisplayName("consultarHorarios")
    void consultarHorarios() {
        when(gestionTiendaService.consultarHorariosTienda(1L)).thenReturn(List.of(new HorarioAtencion()));

        ResponseEntity<List<HorarioAtencion>> res = controller.consultarHorarios(1L);

        assertThat(res.getBody()).hasSize(1);
    }
}
