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
import com.ecomarket.analiticaservice.model.entity.RespaldoBaseDatos;
import com.ecomarket.analiticaservice.model.reference.EstadoRespaldo;
import com.ecomarket.analiticaservice.repository.RespaldoBaseDatosRepository;

@ExtendWith(MockitoExtension.class)
class RespaldoDomainServiceTest {

    @Mock private RespaldoBaseDatosRepository respaldoRepository;

    @InjectMocks private RespaldoDomainService service;

    private final EstadoRespaldo estado = new EstadoRespaldo(1L, "Completado");

    @Test
    void buscarPorIdReturnsWhenFound() {
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, LocalDateTime.now(), 10.0, estado, "/ruta");
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(r));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorIdThrowsWhenNotFound() {
        when(respaldoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarTodosReturnsAll() {
        when(respaldoRepository.findAll()).thenReturn(List.of(new RespaldoBaseDatos()));

        assertEquals(1, service.listarTodos().size());
    }

    @Test
    void buscarPorRangoFechasReturnsFiltered() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        when(respaldoRepository.findByFechaRespaldoBetween(inicio, fin)).thenReturn(List.of(new RespaldoBaseDatos()));

        assertEquals(1, service.buscarPorRangoFechas(inicio, fin).size());
    }

    @Test
    void crearSavesAndReturns() {
        RespaldoBaseDatos r = new RespaldoBaseDatos();
        when(respaldoRepository.save(any(RespaldoBaseDatos.class))).thenReturn(r);

        assertSame(r, service.crear(r));
    }

    @Test
    void crearRespaldoCreatesAndReturns() {
        when(respaldoRepository.save(any(RespaldoBaseDatos.class))).thenAnswer(i -> i.getArgument(0));

        RespaldoBaseDatos result = service.crearRespaldo(estado, 20.0, "/ruta");

        assertEquals(estado, result.getEstado());
        assertEquals(20.0, result.getTamanoMegabytes());
        assertEquals("/ruta", result.getRutaAlmacenamiento());
        assertNotNull(result.getFechaRespaldo());
    }

    @Test
    void actualizarEstadoUpdatesAndReturns() {
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, LocalDateTime.now(), 10.0, estado, "/ruta");
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(r));
        when(respaldoRepository.save(any(RespaldoBaseDatos.class))).thenAnswer(i -> i.getArgument(0));

        EstadoRespaldo nuevo = new EstadoRespaldo(2L, "Fallido");
        RespaldoBaseDatos result = service.actualizarEstado(1L, nuevo);

        assertEquals(nuevo, result.getEstado());
    }
}
