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
import com.ecomarket.analiticaservice.model.entity.MetricaDashboard;
import com.ecomarket.analiticaservice.repository.MetricaDashboardRepository;

@ExtendWith(MockitoExtension.class)
class MetricaDomainServiceTest {

    @Mock private MetricaDashboardRepository metricaRepository;

    @InjectMocks private MetricaDomainService service;

    @Test
    void buscarPorIdReturnsWhenFound() {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 100.0, "texto", LocalDateTime.now());
        when(metricaRepository.findById(1L)).thenReturn(Optional.of(m));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorIdThrowsWhenNotFound() {
        when(metricaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarTodasReturnsAll() {
        when(metricaRepository.findAll()).thenReturn(List.of(new MetricaDashboard()));

        assertEquals(1, service.listarTodas().size());
    }

    @Test
    void buscarPorClaveReturnsWhenFound() {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 100.0, "texto", LocalDateTime.now());
        when(metricaRepository.findByClaveMetrica("ventas")).thenReturn(Optional.of(m));

        assertEquals("ventas", service.buscarPorClave("ventas").getClaveMetrica());
    }

    @Test
    void buscarPorClaveThrowsWhenNotFound() {
        when(metricaRepository.findByClaveMetrica("inexistente")).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> service.buscarPorClave("inexistente"));
    }

    @Test
    void crearSavesAndReturns() {
        when(metricaRepository.save(any(MetricaDashboard.class))).thenAnswer(i -> i.getArgument(0));

        MetricaDashboard result = service.crear("ventas", 100.0, "texto");

        assertEquals("ventas", result.getClaveMetrica());
        assertEquals(100.0, result.getValorNumerico());
        assertEquals("texto", result.getValorTexto());
        assertNotNull(result.getUltimaActualizacion());
    }

    @Test
    void actualizarUpdatesAndReturns() {
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 50.0, "old", LocalDateTime.now());
        when(metricaRepository.findById(1L)).thenReturn(Optional.of(m));
        when(metricaRepository.save(any(MetricaDashboard.class))).thenAnswer(i -> i.getArgument(0));

        MetricaDashboard result = service.actualizar(1L, 200.0, "new");

        assertEquals(200.0, result.getValorNumerico());
        assertEquals("new", result.getValorTexto());
    }
}
