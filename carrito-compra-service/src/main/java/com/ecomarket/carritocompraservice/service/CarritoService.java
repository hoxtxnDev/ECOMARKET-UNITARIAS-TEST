package com.ecomarket.carritocompraservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomarket.carritocompraservice.client.CatalogoInventarioClient;
import com.ecomarket.carritocompraservice.client.LogisticaEnvioClient;
import com.ecomarket.carritocompraservice.client.ProcesoPagoClient;
import com.ecomarket.carritocompraservice.client.RegistroUsuariosClient;
import com.ecomarket.carritocompraservice.dto.ProductoClienteDTO;
import com.ecomarket.carritocompraservice.model.Carrito;
import com.ecomarket.carritocompraservice.model.ItemCarrito;
import com.ecomarket.carritocompraservice.repository.CarritoRepository;
import com.ecomarket.carritocompraservice.repository.ItemCarritoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CarritoService {
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CatalogoInventarioClient catalogoClient;
    private final RegistroUsuariosClient registroUsuariosClient;
    private final ProcesoPagoClient procesoPagoClient;
    private final LogisticaEnvioClient logisticaEnvioClient;

    public Carrito obtenerCarritoActivo(Long clienteId) {
        registroUsuariosClient.validarCliente(clienteId);
        
        // 1. Intentar obtener el carrito que ya está activo
        return carritoRepository.findByClienteIdAndActivoTrue(clienteId)
                .orElseGet(() -> {
                    // 2. Si no hay uno activo, buscar el último que NO haya sido cerrado definitivamente
                    return carritoRepository.findFirstByClienteIdAndCerradoFalseOrderByIdDesc(clienteId)
                            .orElseGet(() -> {
                                // 3. Si no existe ningún carrito abierto en la historia, crear uno nuevo
                                Carrito nuevo = new Carrito();
                                nuevo.setClienteId(clienteId);
                                nuevo.setActivo(false);
                                nuevo.setCerrado(false);
                                return carritoRepository.save(nuevo);
                            });
                });
    }

    public Carrito anadirProducto(Long clienteId, Long productoId, Integer cantidad) {
        registroUsuariosClient.validarCliente(clienteId);
        ProductoClienteDTO producto = catalogoClient.obtenerProducto(productoId);
        
        Carrito carrito = obtenerCarritoActivo(clienteId);
        
        // Reactivamos el carrito al agregar productos
        carrito.setActivo(true);

        itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .ifPresentOrElse(
                        item -> {
                            item.setCantidad(item.getCantidad() + cantidad);
                        },
                        () -> {
                            ItemCarrito item = new ItemCarrito();
                            item.setCarrito(carrito);
                            item.setProductoId(productoId);
                            item.setCantidad(cantidad);
                            item.setPrecioUnitarioAgregado(producto.getPrecioBase());
                            item.setPosicion(carrito.getItems().size() + 1);
                            carrito.getItems().add(item);
                        });

        carrito.setFechaUltimaModificacion(LocalDateTime.now());
        carrito.setSubtotal(carrito.calcularTotal());
        return carritoRepository.save(carrito);
    }

    public Carrito removerProducto(Long clienteId, Long itemId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);
        
        ItemCarrito itemABorrar = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        int posicionBorrada = itemABorrar.getPosicion();
        carrito.getItems().remove(itemABorrar);
        
        // Recalcular la posición de los items siguientes
        carrito.getItems().stream()
                .filter(i -> i.getPosicion() > posicionBorrada)
                .forEach(i -> i.setPosicion(i.getPosicion() - 1));

        if (carrito.getItems().isEmpty()) {
            carrito.setActivo(false);
        }

        carrito.setFechaUltimaModificacion(LocalDateTime.now());
        carrito.setSubtotal(carrito.calcularTotal());
        return carritoRepository.save(carrito);
    }

    public Carrito seleccionarMetodoPago(Long clienteId, Long metodoPagoId) {
        procesoPagoClient.validarMetodoPago(metodoPagoId);
        Carrito carrito = obtenerCarritoActivo(clienteId);
        carrito.setMetodoPagoId(metodoPagoId);
        carrito.setFechaUltimaModificacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    public Carrito seleccionarEnvio(Long clienteId, Long metodoEnvioId) {
        logisticaEnvioClient.validarMetodoEnvio(metodoEnvioId);
        Carrito carrito = obtenerCarritoActivo(clienteId);
        carrito.setMetodoEnvioId(metodoEnvioId);
        carrito.setFechaUltimaModificacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    public boolean vaciarCarrito(Long clienteId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);
        carrito.getItems().clear();
        carrito.setActivo(false);
        carrito.setSubtotal(0.0);
        // Mantener la fecha como null si el carrito nunca se modificó, 
        // pero al vaciarlo es una modificación.
        carrito.setFechaUltimaModificacion(LocalDateTime.now());
        carritoRepository.save(carrito);
        return true;
    }

    public List<Carrito> listarTodos() {
        return carritoRepository.findAll();
    }

    public void cerrarCarrito(Long clienteId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);
        carrito.setActivo(false);
        // Marcado como cerrado definitivamente (historial)
        carrito.setCerrado(true);
        carritoRepository.save(carrito);
    }
}
