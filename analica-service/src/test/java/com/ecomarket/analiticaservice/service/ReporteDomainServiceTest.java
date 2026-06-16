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
import com.ecomarket.analiticaservice.model.entity.Reporte;
import com.ecomarket.analiticaservice.model.reference.EstadoReporte;
import com.ecomarket.analiticaservice.model.reference.TipoReporte;
import com.ecomarket.analiticaservice.repository.ReporteRepository;

@ExtendWith(MockitoExtension.class)
class ReporteDomainServiceTest {

    @Mock private ReporteRepository reporteRepository;

    @InjectMocks private ReporteDomainService service;

    private final EstadoReporte estado = new EstadoReporte(1L, "Pendiente");
    private final TipoReporte tipo = new TipoReporte(1L, "Usuarios");

    @Test
    void buscarPorIdReturnsWhenFound() {
        Reporte r = new Reporte(1L, 10L, tipo, estado, LocalDateTime.now(), null, null);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorIdThrowsWhenNotFound() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarTodosReturnsAll() {
        when(reporteRepository.findAll()).thenReturn(List.of(new Reporte()));

        assertEquals(1, service.listarTodos().size());
    }

    @Test
    void buscarPorSolicitanteReturnsFiltered() {
        when(reporteRepository.findBySolicitanteId(10L)).thenReturn(List.of(new Reporte()));

        assertEquals(1, service.buscarPorSolicitante(10L).size());
    }

    @Test
    void buscarPorRangoFechasReturnsFiltered() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        when(reporteRepository.findByFechaGeneracionBetween(inicio, fin)).thenReturn(List.of(new Reporte()));

        assertEquals(1, service.buscarPorRangoFechas(inicio, fin).size());
    }

    @Test
    void crearSavesAndReturns() {
        Reporte r = new Reporte();
        when(reporteRepository.save(any(Reporte.class))).thenReturn(r);

        assertSame(r, service.crear(r));
    }

    @Test
    void actualizarEstadoUpdatesAllFields() {
        Reporte r = new Reporte(1L, 10L, tipo, estado, LocalDateTime.now(), null, null);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArgument(0));

        EstadoReporte nuevoEstado = new EstadoReporte(2L, "Completado");
        Reporte result = service.actualizarEstado(1L, nuevoEstado, "/url", 100);

        assertEquals(nuevoEstado, result.getEstado());
        assertEquals("/url", result.getUrlArchivoResultado());
        assertEquals(100, result.getTotalRegistrosProcesados());
    }

    @Test
    void actualizarEstadoSkipsNullUrlAndTotal() {
        Reporte r = new Reporte(1L, 10L, tipo, estado, LocalDateTime.now(), null, null);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArgument(0));

        Reporte result = service.actualizarEstado(1L, estado, null, null);

        assertNull(result.getUrlArchivoResultado());
        assertNull(result.getTotalRegistrosProcesados());
    }

    @Test
    void crearReporteCreatesAndReturns() {
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArgument(0));

        Reporte result = service.crearReporte(10L, tipo, estado);

        assertEquals(10L, result.getSolicitanteId());
        assertEquals(tipo, result.getTipo());
        assertEquals(estado, result.getEstado());
        assertNotNull(result.getFechaGeneracion());
    }
}
