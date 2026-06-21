package com.ecomarket.catalogoinventarioservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioStockRepository inventarioRepository;
    private final StockGlobalRepository stockGlobalRepository;
    private final ProductoRepository productoRepository;
    private final GestionTiendaClient gestionTiendaClient;

    private SucursalDTO validarSucursal(Long sucursalId) {
        try {
            return gestionTiendaClient.obtenerSucursal(sucursalId);
        } catch (Exception e) {
            throw new NoExisteEnBdException("La sucursal con ID " + sucursalId + " no existe.");
        }
    }

    private Producto validarProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new NoExisteEnBdException("Producto " + productoId + " no encontrado."));
    }

    public List<InventarioStock> listarTodos() {
        return inventarioRepository.findAll();
    }

    public void ajustarStock(Long productoId, Long sucursalId, Integer nuevaCantidad) {
        validarProducto(productoId);
        validarSucursal(sucursalId);
        InventarioStock stock = inventarioRepository
                .findTopByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new NoExisteEnBdException(
                        "No hay inventario en la sucursal " + sucursalId + " para el producto " + productoId + "."));
        stock.setCantidadDisponible(nuevaCantidad);
        inventarioRepository.save(stock);
    }

    public StockGlobal ingresarStockGlobal(Long productoId, Integer cantidad) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es requerido.");
        }
        validarProducto(productoId);

        StockGlobal stockGlobal = stockGlobalRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    StockGlobal nuevo = new StockGlobal();
                    nuevo.setProductoId(productoId);
                    nuevo.setCantidadDisponible(0);
                    return nuevo;
                });

        stockGlobal.incrementarStock(cantidad);
        return stockGlobalRepository.save(stockGlobal);
    }

    public MensajeDTO transferirStockDesdeGlobal(Long productoId, Long sucursalId, Integer cantidad) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es requerido.");
        }
        if (sucursalId == null) {
            throw new IllegalArgumentException("El ID de la sucursal es requerido.");
        }
        validarProducto(productoId);
        SucursalDTO sucursal = validarSucursal(sucursalId);

        StockGlobal stockGlobal = stockGlobalRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NoExisteEnBdException("No hay stock global registrado para el producto " + productoId + "."));

        if (!stockGlobal.hayStock(cantidad)) {
            throw new YaExisteEnBdException("Stock global insuficiente del producto " + productoId
                    + ". Disponible: " + stockGlobal.getCantidadDisponible() + ", solicitado: " + cantidad + ".");
        }

        stockGlobal.disminuirStock(cantidad);
        stockGlobalRepository.save(stockGlobal);

        InventarioStock inventario = inventarioRepository
                .findTopByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseGet(() -> {
                    InventarioStock nuevo = new InventarioStock();
                    nuevo.setProductoId(productoId);
                    nuevo.setSucursalId(sucursalId);
                    nuevo.setCantidadDisponible(0);
                    nuevo.setCantidadReservada(0);
                    return nuevo;
                });

        inventario.setCantidadDisponible(inventario.getCantidadDisponible() + cantidad);
        inventario.setUltimaReposicion(LocalDateTime.now());
        inventarioRepository.save(inventario);

        return new MensajeDTO("Se ha asignado desde stock global " + cantidad
                + " unidades del producto ID " + productoId
                + " a la sucursal " + sucursal.getNombre() + " (ID " + sucursalId + ").");
    }

    public boolean verificarDisponibilidad(Long productoId, Long sucursalId, Integer cantidad) {
        return inventarioRepository.findTopByProductoIdAndSucursalId(productoId, sucursalId)
                .map(s -> s.hayStock(cantidad))
                .orElse(false);
    }

    public StockGlobal consultarStockGlobal(Long productoId) {
        return stockGlobalRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NoExisteEnBdException("No hay stock global registrado para el producto " + productoId + "."));
    }

    public List<InventarioStock> consultarInventarioGlobal(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    public List<InventarioStock> consultarInventarioPorSucursal(Long sucursalId, Long productoId) {
        return inventarioRepository.findBySucursalIdAndProductoId(sucursalId, productoId);
    }

    public void reservarStock(Long productoId, Long sucursalId, Integer cantidad) {
        validarProducto(productoId);
        validarSucursal(sucursalId);

        InventarioStock stock = inventarioRepository
                .findTopByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new NoExisteEnBdException(
                        "No hay inventario en la sucursal " + sucursalId + " para el producto " + productoId + "."));

        if (!stock.hayStock(cantidad)) {
            throw new YaExisteEnBdException("Stock insuficiente en la sucursal " + sucursalId
                    + " del producto " + productoId + ". Disponible: " + stock.getCantidadDisponible()
                    + ", solicitado: " + cantidad + ".");
        }

        stock.setCantidadDisponible(stock.getCantidadDisponible() - cantidad);
        stock.setCantidadReservada(stock.getCantidadReservada() + cantidad);
        inventarioRepository.save(stock);

        if (stock.getStockMinimoAlerta() != null
                && stock.getCantidadDisponible() <= stock.getStockMinimoAlerta()) {
            gestionTiendaClient.notificarStockBajo(
                stock.getSucursalId(), productoId, stock.getCantidadDisponible());
        }
    }

    public void liberarStock(Long productoId, Long sucursalId, Integer cantidad) {
        validarProducto(productoId);
        validarSucursal(sucursalId);

        InventarioStock stock = inventarioRepository
                .findTopByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new NoExisteEnBdException(
                        "No hay inventario en la sucursal " + sucursalId + " para el producto " + productoId + "."));

        if (stock.getCantidadReservada() < cantidad) {
            throw new YaExisteEnBdException("No hay suficientes unidades reservadas en la sucursal " + sucursalId
                    + " del producto " + productoId + ". Reservadas: " + stock.getCantidadReservada()
                    + ", solicitado: " + cantidad + ".");
        }

        stock.setCantidadReservada(stock.getCantidadReservada() - cantidad);
        stock.setCantidadDisponible(stock.getCantidadDisponible() + cantidad);
        inventarioRepository.save(stock);
    }
}
