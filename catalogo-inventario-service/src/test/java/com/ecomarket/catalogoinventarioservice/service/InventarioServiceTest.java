package com.ecomarket.catalogoinventarioservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import com.ecomarket.catalogoinventarioservice.dto.MensajeDTO;
import com.ecomarket.catalogoinventarioservice.dto.SucursalDTO;
import com.ecomarket.catalogoinventarioservice.exception.NoExisteEnBdException;
import com.ecomarket.catalogoinventarioservice.exception.YaExisteEnBdException;
import com.ecomarket.catalogoinventarioservice.model.InventarioStock;
import com.ecomarket.catalogoinventarioservice.model.Producto;
import com.ecomarket.catalogoinventarioservice.model.StockGlobal;
import com.ecomarket.catalogoinventarioservice.repository.InventarioStockRepository;
import com.ecomarket.catalogoinventarioservice.repository.ProductoRepository;
import com.ecomarket.catalogoinventarioservice.repository.StockGlobalRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock private InventarioStockRepository inventarioRepository;
    @Mock private StockGlobalRepository stockGlobalRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private GestionTiendaClient gestionTiendaClient;

    @InjectMocks private InventarioService inventarioService;

    private Producto producto;
    private InventarioStock stock;
    private SucursalDTO sucursal;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);

        stock = new InventarioStock();
        stock.setId(1L);
        stock.setProductoId(1L);
        stock.setSucursalId(10L);
        stock.setCantidadDisponible(50);
        stock.setCantidadReservada(10);

        sucursal = new SucursalDTO();
        sucursal.setId(10L);
        sucursal.setNombre("Sucursal Centro");
    }

    @Test
    void listarTodosReturnsAll() {
        when(inventarioRepository.findAll()).thenReturn(List.of(stock));

        assertEquals(1, inventarioService.listarTodos().size());
    }

    @Test
    void verificarDisponibilidadReturnsTrueWhenStockAvailable() {
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));

        assertTrue(inventarioService.verificarDisponibilidad(1L, 10L, 30));
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenInsufficientStock() {
        stock.setCantidadDisponible(5);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));

        assertFalse(inventarioService.verificarDisponibilidad(1L, 10L, 30));
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenNoStockEntries() {
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.empty());

        assertFalse(inventarioService.verificarDisponibilidad(1L, 10L, 10));
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
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10L, 10);

        assertEquals(40, stock.getCantidadDisponible());
        assertEquals(20, stock.getCantidadReservada());
    }

    @Test
    void reservarStockNotifiesWhenBelowMinimum() {
        stock.setStockMinimoAlerta(45);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10L, 10);

        verify(gestionTiendaClient).notificarStockBajo(10L, 1L, 40);
    }

    @Test
    void reservarStockDoesNotNotifyWhenAboveMinimum() {
        stock.setStockMinimoAlerta(30);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10L, 10);

        verify(gestionTiendaClient, never()).notificarStockBajo(anyLong(), anyLong(), anyInt());
    }

    @Test
    void reservarStockDoesNotNotifyWhenStockMinimoAlertaIsNull() {
        stock.setStockMinimoAlerta(null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.reservarStock(1L, 10L, 10);

        verify(gestionTiendaClient, never()).notificarStockBajo(anyLong(), anyLong(), anyInt());
    }

    @Test
    void reservarStockThrowsWhenNoStockEntry() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventarioService.reservarStock(1L, 10L, 10));
    }

    @Test
    void reservarStockThrowsWhenInsufficientStock() {
        stock.setCantidadDisponible(5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));

        assertThrows(RuntimeException.class, () -> inventarioService.reservarStock(1L, 10L, 10));
    }

    @Test
    void liberarStockIncrementsAvailableAndDecrementsReserved() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.liberarStock(1L, 10L, 5);

        assertEquals(55, stock.getCantidadDisponible());
        assertEquals(5, stock.getCantidadReservada());
    }

    @Test
    void liberarStockThrowsWhenNotEnoughReserved() {
        stock.setCantidadReservada(2);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));

        assertThrows(RuntimeException.class, () -> inventarioService.liberarStock(1L, 10L, 10));
    }

    @Test
    void liberarStockThrowsWhenNoStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventarioService.liberarStock(1L, 10L, 10));
    }

    @Test
    void ajustarStockUpdatesQuantity() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.ajustarStock(1L, 10L, 100);

        assertEquals(100, stock.getCantidadDisponible());
    }

    @Test
    void ajustarStockThrowsWhenStockNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(99L)).thenReturn(sucursal);
        when(inventarioRepository.findTopByProductoIdAndSucursalId(99L, 99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventarioService.ajustarStock(99L, 99L, 100));
    }

    @Test
    void ajustarStockThrowsWhenProductNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventarioService.ajustarStock(99L, 10L, 100));
    }

    @Test
    void ajustarStockThrowsWhenSucursalNotFound() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(99L)).thenThrow(new RuntimeException("Sucursal no encontrada"));

        assertThrows(RuntimeException.class, () -> inventarioService.ajustarStock(1L, 99L, 100));
    }

    @Test
    void ingresarStockGlobalCreatesNewWhenNoneExists() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.empty());
        when(stockGlobalRepository.save(any(StockGlobal.class))).thenAnswer(i -> i.getArgument(0));

        StockGlobal result = inventarioService.ingresarStockGlobal(1L, 50);

        assertEquals(1L, result.getProductoId());
        assertEquals(50, result.getCantidadDisponible());
        verify(stockGlobalRepository).save(any(StockGlobal.class));
    }

    @Test
    void ingresarStockGlobalIncrementsExisting() {
        StockGlobal existente = new StockGlobal();
        existente.setId(1L);
        existente.setProductoId(1L);
        existente.setCantidadDisponible(100);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.of(existente));
        when(stockGlobalRepository.save(any(StockGlobal.class))).thenAnswer(i -> i.getArgument(0));

        StockGlobal result = inventarioService.ingresarStockGlobal(1L, 50);

        assertEquals(150, result.getCantidadDisponible());
    }

    @Test
    void ingresarStockGlobalThrowsWhenProductoIdNull() {
        assertThrows(IllegalArgumentException.class, () -> inventarioService.ingresarStockGlobal(null, 50));
    }

    @Test
    void ingresarStockGlobalThrowsWhenProductNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> inventarioService.ingresarStockGlobal(99L, 50));
    }

    @Test
    void transferirStockDesdeGlobalTransfersToExistingInventario() {
        StockGlobal globalStock = new StockGlobal();
        globalStock.setId(1L);
        globalStock.setProductoId(1L);
        globalStock.setCantidadDisponible(200);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.of(globalStock));
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.of(stock));
        when(stockGlobalRepository.save(any(StockGlobal.class))).thenAnswer(i -> i.getArgument(0));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        MensajeDTO result = inventarioService.transferirStockDesdeGlobal(1L, 10L, 50);

        assertEquals(150, globalStock.getCantidadDisponible());
        assertEquals(100, stock.getCantidadDisponible());
        assertEquals("Se ha asignado desde stock global 50 unidades del producto ID 1 a la sucursal Sucursal Centro (ID 10).", result.getMensaje());
    }

    @Test
    void transferirStockDesdeGlobalCreatesNewInventarioWhenNoneExists() {
        StockGlobal globalStock = new StockGlobal();
        globalStock.setId(1L);
        globalStock.setProductoId(1L);
        globalStock.setCantidadDisponible(200);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.of(globalStock));
        when(inventarioRepository.findTopByProductoIdAndSucursalId(1L, 10L)).thenReturn(Optional.empty());
        when(stockGlobalRepository.save(any(StockGlobal.class))).thenAnswer(i -> i.getArgument(0));
        when(inventarioRepository.save(any(InventarioStock.class))).thenAnswer(i -> i.getArgument(0));

        MensajeDTO result = inventarioService.transferirStockDesdeGlobal(1L, 10L, 50);

        assertEquals(150, globalStock.getCantidadDisponible());
        assertEquals("Se ha asignado desde stock global 50 unidades del producto ID 1 a la sucursal Sucursal Centro (ID 10).", result.getMensaje());
        verify(inventarioRepository).save(any(InventarioStock.class));
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenProductoIdNull() {
        assertThrows(IllegalArgumentException.class, () -> inventarioService.transferirStockDesdeGlobal(null, 10L, 50));
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenSucursalIdNull() {
        assertThrows(IllegalArgumentException.class, () -> inventarioService.transferirStockDesdeGlobal(1L, null, 50));
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenProductNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> inventarioService.transferirStockDesdeGlobal(99L, 10L, 50));
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenSucursalNotFound() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(99L)).thenThrow(new RuntimeException("Sucursal no encontrada"));

        assertThrows(NoExisteEnBdException.class, () -> inventarioService.transferirStockDesdeGlobal(1L, 99L, 50));
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenNoGlobalStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> inventarioService.transferirStockDesdeGlobal(1L, 10L, 50));
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenGlobalStockInsufficient() {
        StockGlobal globalStock = new StockGlobal();
        globalStock.setId(1L);
        globalStock.setProductoId(1L);
        globalStock.setCantidadDisponible(30);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(gestionTiendaClient.obtenerSucursal(10L)).thenReturn(sucursal);
        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.of(globalStock));

        assertThrows(YaExisteEnBdException.class, () -> inventarioService.transferirStockDesdeGlobal(1L, 10L, 50));
    }

    @Test
    void consultarStockGlobalReturnsStockWhenFound() {
        StockGlobal globalStock = new StockGlobal();
        globalStock.setId(1L);
        globalStock.setProductoId(1L);
        globalStock.setCantidadDisponible(200);

        when(stockGlobalRepository.findByProductoId(1L)).thenReturn(Optional.of(globalStock));

        StockGlobal result = inventarioService.consultarStockGlobal(1L);

        assertEquals(1L, result.getProductoId());
        assertEquals(200, result.getCantidadDisponible());
    }

    @Test
    void consultarStockGlobalThrowsWhenNotFound() {
        when(stockGlobalRepository.findByProductoId(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> inventarioService.consultarStockGlobal(99L));
    }

    @Test
    void ingresarStockGlobalThrowsWhenProductoIdNullWithCorrectMessage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> inventarioService.ingresarStockGlobal(null, 50));
        assertEquals("El ID del producto es requerido.", e.getMessage());
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenProductoIdNullWithCorrectMessage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> inventarioService.transferirStockDesdeGlobal(null, 10L, 50));
        assertEquals("El ID del producto es requerido.", e.getMessage());
    }

    @Test
    void transferirStockDesdeGlobalThrowsWhenSucursalIdNullWithCorrectMessage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> inventarioService.transferirStockDesdeGlobal(1L, null, 50));
        assertEquals("El ID de la sucursal es requerido.", e.getMessage());
    }
}
