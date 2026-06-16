package com.ecomarket.catalogoinventarioservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecomarket.catalogoinventarioservice.client.GestionTiendaClient;
import com.ecomarket.catalogoinventarioservice.model.InventarioStock;
import com.ecomarket.catalogoinventarioservice.model.Producto;
import com.ecomarket.catalogoinventarioservice.repository.InventarioStockRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock private InventarioStockRepository inventarioRepository;
    @Mock private GestionTiendaClient gestionTiendaClient;

    @InjectMocks private InventarioService inventarioService;

    private Producto producto;
    private InventarioStock stock;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);

        stock = new InventarioStock();
        stock.setId(1L);
        stock.setProducto(producto);
        stock.setSucursalId(10L);
        stock.setCantidadDisponible(50);
        stock.setCantidadReservada(10);
    }

    @Test
    void listarTodosReturnsAll() {
        when(inventarioRepository.findAll()).thenReturn(List.of(stock));

        assertEquals(1, inventarioService.listarTodos().size());
    }

    @Test
    void verificarDisponibilidadReturnsTrueWhenStockAvailable() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));

        assertTrue(inventarioService.verificarDisponibilidad(1L, 30));
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenInsufficientStock() {
        stock.setCantidadDisponible(5);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));

        assertFalse(inventarioService.verificarDisponibilidad(1L, 30));
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenNoStockEntries() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of());

        assertFalse(inventarioService.verificarDisponibilidad(1L, 10));
    }

    @Test
    void consultarInventarioGlobalReturnsStockByProduct() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));

        List<InventarioStock> result = inventarioService.consultarInventarioGlobal(1L);

        assertEquals(1, result.size());
    }

    @Test
    void consultarInventarioPorSucursalReturnsFiltered() {
        when(inventarioRepository.findBySucursalIdAndProductoId(10L, 1L)).thenReturn(List.of(stock));

        List<InventarioStock> result = inventarioService.consultarInventarioPorSucursal(10L, 1L);

        assertEquals(1, result.size());
    }

    @Test
    void reservarStockDecrementsAvailableAndIncrementsReserved() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        boolean result = inventarioService.reservarStock(1L, 10);

        assertTrue(result);
        assertEquals(40, stock.getCantidadDisponible());
        assertEquals(20, stock.getCantidadReservada());
    }

    @Test
    void reservarStockNotifiesWhenBelowMinimum() {
        stock.setStockMinimoAlerta(45);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10);

        verify(gestionTiendaClient).notificarStockBajo(10L, 1L, 40);
    }

    @Test
    void reservarStockDoesNotNotifyWhenAboveMinimum() {
        stock.setStockMinimoAlerta(30);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10);

        verify(gestionTiendaClient, never()).notificarStockBajo(anyLong(), anyLong(), anyInt());
    }

    @Test
    void reservarStockDoesNotNotifyWhenStockMinimoAlertaIsNull() {
        stock.setStockMinimoAlerta(null);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10);

        verify(gestionTiendaClient, never()).notificarStockBajo(anyLong(), anyLong(), anyInt());
    }

    @Test
    void reservarStockReturnsFalseWhenNoEntryHasEnoughStock() {
        stock.setCantidadDisponible(5);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));

        assertFalse(inventarioService.reservarStock(1L, 10));
    }

    @Test
    void reservarStockReturnsFalseWhenNoStockEntries() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of());

        assertFalse(inventarioService.reservarStock(1L, 10));
    }

    @Test
    void liberarStockIncrementsAvailableAndDecrementsReserved() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        boolean result = inventarioService.liberarStock(1L, 5);

        assertTrue(result);
        assertEquals(55, stock.getCantidadDisponible());
        assertEquals(5, stock.getCantidadReservada());
    }

    @Test
    void liberarStockReturnsFalseWhenNotEnoughReserved() {
        stock.setCantidadReservada(2);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(stock));

        assertFalse(inventarioService.liberarStock(1L, 10));
    }

    @Test
    void liberarStockReturnsFalseWhenNoStock() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of());

        assertFalse(inventarioService.liberarStock(1L, 10));
    }

    @Test
    void ajustarStockUpdatesQuantity() {
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        InventarioStock result = inventarioService.ajustarStock(1L, 10L, 100);

        assertEquals(100, result.getCantidadDisponible());
    }

    @Test
    void ajustarStockThrowsWhenStockNotFound() {
        when(inventarioRepository.findTopByProductoIdAndSucursalId(99L, 99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventarioService.ajustarStock(99L, 99L, 100));
    }

}
