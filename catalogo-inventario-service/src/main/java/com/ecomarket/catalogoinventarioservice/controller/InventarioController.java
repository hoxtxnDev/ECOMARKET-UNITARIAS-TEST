package com.ecomarket.catalogoinventarioservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomarket.catalogoinventarioservice.dto.CantidadDTO;
import com.ecomarket.catalogoinventarioservice.dto.MensajeDTO;
import com.ecomarket.catalogoinventarioservice.model.InventarioStock;
import com.ecomarket.catalogoinventarioservice.model.StockGlobal;
import com.ecomarket.catalogoinventarioservice.service.InventarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioStock>> listarInventario() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @GetMapping("/disponibilidad/{productoId}/{sucursalId}/{cantidad}")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId,
            @PathVariable Integer cantidad) {
        return ResponseEntity.ok(inventarioService.verificarDisponibilidad(productoId, sucursalId, cantidad));
    }

    @GetMapping("/stock-global/{productoId}")
    public ResponseEntity<StockGlobal> consultarStockGlobal(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.consultarStockGlobal(productoId));
    }

    @GetMapping("/global/{productoId}")
    public ResponseEntity<List<InventarioStock>> consultarInventarioGlobal(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.consultarInventarioGlobal(productoId));
    }

    @GetMapping("/sucursal/{sucursalId}/producto/{productoId}")
    public ResponseEntity<List<InventarioStock>> consultarPorSucursal(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.consultarInventarioPorSucursal(sucursalId, productoId));
    }

    @PostMapping("/ingresar")
    public ResponseEntity<StockGlobal> ingresarStockGlobal(@RequestBody @Valid CantidadDTO dto) {
        return ResponseEntity.ok(inventarioService.ingresarStockGlobal(dto.getProductoId(), dto.getCantidad()));
    }

    @PostMapping("/transferir")
    public ResponseEntity<MensajeDTO> transferirStockDesdeGlobal(@RequestBody @Valid CantidadDTO dto) {
        return ResponseEntity.ok(inventarioService.transferirStockDesdeGlobal(dto.getProductoId(), dto.getSucursalId(), dto.getCantidad()));
    }

    @PostMapping("/reservar/{productoId}/{sucursalId}")
    public ResponseEntity<Void> reservarStock(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId,
            @RequestBody @Valid CantidadDTO dto) {
        inventarioService.reservarStock(productoId, sucursalId, dto.getCantidad());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/liberar/{productoId}/{sucursalId}")
    public ResponseEntity<Void> liberarStock(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId,
            @RequestBody @Valid CantidadDTO dto) {
        inventarioService.liberarStock(productoId, sucursalId, dto.getCantidad());
        return ResponseEntity.ok().build();
    }

    // Se utiliza en caso de "compra", de aqui es donde se va ajustando respecto a las compras.
    @PutMapping("/ajustar/{productoId}/sucursal/{sucursalId}")
    public ResponseEntity<Void> ajustarStock(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId,
            @RequestBody @Valid CantidadDTO dto) {
        inventarioService.ajustarStock(productoId, sucursalId, dto.getCantidad());
        return ResponseEntity.ok().build();
    }
}
