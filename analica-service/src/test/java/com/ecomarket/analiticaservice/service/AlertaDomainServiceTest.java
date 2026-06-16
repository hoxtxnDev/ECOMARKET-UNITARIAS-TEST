package com.ecomarket.analiticaservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecomarket.analiticaservice.exception.NoExisteEnBdException;
import com.ecomarket.analiticaservice.model.entity.AlertaSistema;
import com.ecomarket.analiticaservice.model.reference.NivelAlerta;
import com.ecomarket.analiticaservice.repository.AlertaSistemaRepository;

@ExtendWith(MockitoExtension.class)
class AlertaDomainServiceTest {

    @Mock private AlertaSistemaRepository alertaRepository;

    @InjectMocks private AlertaDomainService service;

    @Test
    void buscarPorIdReturnsAlertaWhenFound() {
        AlertaSistema alerta = new AlertaSistema(1L, new NivelAlerta(), "Mensaje", "Modulo", LocalDateTime.now(), false);
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alerta));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorIdThrowsWhenNotFound() {
        when(alertaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarTodasReturnsAll() {
        when(alertaRepository.findAll()).thenReturn(List.of(new AlertaSistema()));

        assertEquals(1, service.listarTodas().size());
    }

    @Test
    void buscarPorResueltaReturnsFiltered() {
        when(alertaRepository.findByResuelta(true)).thenReturn(List.of(new AlertaSistema()));

        assertEquals(1, service.buscarPorResuelta(true).size());
    }

    @Test
    void buscarPorModuloReturnsFiltered() {
        when(alertaRepository.findByModuloOrigen("Modulo")).thenReturn(List.of(new AlertaSistema()));

        assertEquals(1, service.buscarPorModulo("Modulo").size());
    }

    @Test
    void crearSavesAndReturns() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        when(alertaRepository.save(any(AlertaSistema.class))).thenAnswer(i -> i.getArgument(0));

        AlertaSistema result = service.crear(nivel, "Mensaje", "Modulo");

        assertEquals("Mensaje", result.getMensaje());
        assertEquals("Modulo", result.getModuloOrigen());
        assertFalse(result.getResuelta());
        assertNotNull(result.getFechaAlerta());
    }

    @Test
    void marcarResueltaUpdatesAndReturns() {
        AlertaSistema alerta = new AlertaSistema(1L, new NivelAlerta(), "Mensaje", "Modulo", LocalDateTime.now(), false);
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alerta));
        when(alertaRepository.save(any(AlertaSistema.class))).thenAnswer(i -> i.getArgument(0));

        AlertaSistema result = service.marcarResuelta(1L);

        assertTrue(result.getResuelta());
    }
}
