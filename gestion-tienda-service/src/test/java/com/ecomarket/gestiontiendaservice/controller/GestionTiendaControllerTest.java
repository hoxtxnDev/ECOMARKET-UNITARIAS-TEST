package com.ecomarket.gestiontiendaservice.controller;

import com.ecomarket.gestiontiendaservice.model.*;
import com.ecomarket.gestiontiendaservice.service.GestionTiendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
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
        when(gestionTiendaService.registrarSucursal(anyString(), anyString(), anyString(), anyLong())).thenReturn(s);

        ResponseEntity<Sucursal> res = controller.registrarSucursal("Centro", "Av 1", "412345", 10L);

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
        when(gestionTiendaService.asignarTareaPersonal(anyLong(), anyLong(), anyString(), anyString(), any())).thenReturn(t);

        ResponseEntity<TareaPersonal> res = controller.asignarTarea(5L, 1L, "Tit", "Desc", LocalDateTime.now());

        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("actualizarEstadoTarea")
    void actualizarEstadoTarea() {
        TareaPersonal t = new TareaPersonal();
        t.setId(1L);
        when(gestionTiendaService.actualizarEstadoTarea(anyLong(), any())).thenReturn(t);

        ResponseEntity<TareaPersonal> res = controller.actualizarEstadoTarea(1L, new EstadoTareaPersonal());

        assertThat(res.getBody().getId()).isEqualTo(1L);
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
