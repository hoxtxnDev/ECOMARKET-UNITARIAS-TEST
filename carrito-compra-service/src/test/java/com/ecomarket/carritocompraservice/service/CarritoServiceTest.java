package com.ecomarket.carritocompraservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecomarket.carritocompraservice.client.CatalogoInventarioClient;
import com.ecomarket.carritocompraservice.client.LogisticaEnvioClient;
import com.ecomarket.carritocompraservice.client.ProcesoPagoClient;
import com.ecomarket.carritocompraservice.client.RegistroUsuariosClient;
import com.ecomarket.carritocompraservice.dto.MetodoEnvioDTO;
import com.ecomarket.carritocompraservice.dto.MetodoPagoDTO;
import com.ecomarket.carritocompraservice.dto.ProductoClienteDTO;
import com.ecomarket.carritocompraservice.model.Carrito;
import com.ecomarket.carritocompraservice.model.ItemCarrito;
import com.ecomarket.carritocompraservice.repository.CarritoRepository;
import com.ecomarket.carritocompraservice.repository.ItemCarritoRepository;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private ItemCarritoRepository itemCarritoRepository;
    @Mock private CatalogoInventarioClient catalogoClient;
    @Mock private RegistroUsuariosClient registroUsuariosClient;
    @Mock private ProcesoPagoClient procesoPagoClient;
    @Mock private LogisticaEnvioClient logisticaEnvioClient;

    @InjectMocks private CarritoService service;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(registroUsuariosClient).validarCliente(anyLong());
    }

    private Carrito carritoActivo(Long carritoId, Long clienteId) {
        Carrito c = new Carrito();
        c.setId(carritoId);
        c.setClienteId(clienteId);
        c.setActivo(true);
        c.setSubtotal(0.0);
        c.setItems(new ArrayList<>());
        return c;
    }

    private ProductoClienteDTO producto(Long id, Double precio) {
        return new ProductoClienteDTO(id, "SKU-" + id, "Producto " + id, precio, "Desc", null);
    }

    private ItemCarrito item(Long itemId, Long carritoId, Long productoId, int cantidad, double precio) {
        Carrito c = new Carrito();
        c.setId(carritoId);
        ItemCarrito item = new ItemCarrito();
        item.setId(itemId);
        item.setCarrito(c);
        item.setProductoId(productoId);
        item.setCantidad(cantidad);
        item.setPrecioUnitarioAgregado(precio);
        return item;
    }

    @Nested
    @DisplayName("obtenerCarritoActivo")
    class ObtenerCarritoActivo {

        @Test
        @DisplayName("retorna el carrito existente si el cliente ya tiene uno activo")
        void retornaCarritoExistente() {
            Carrito carrito = carritoActivo(1L, 10L);
            when(carritoRepository.findByClienteIdAndActivoTrue(10L))
                    .thenReturn(Optional.of(carrito));

            Carrito resultado = service.obtenerCarritoActivo(10L);

            assertThat(resultado.getId()).isEqualTo(1L);
            verify(carritoRepository, never()).save(any());
        }

        @Test
        @DisplayName("retorna el ultimo carrito no cerrado si no hay uno activo")
        void retornaUltimoCarritoNoCerrado() {
            Carrito carritoExistente = carritoActivo(1L, 10L);
            carritoExistente.setActivo(false);
            when(carritoRepository.findByClienteIdAndActivoTrue(10L))
                    .thenReturn(Optional.empty());
            when(carritoRepository.findFirstByClienteIdAndCerradoFalseOrderByIdDesc(10L))
                    .thenReturn(Optional.of(carritoExistente));

            Carrito resultado = service.obtenerCarritoActivo(10L);

            assertThat(resultado.getId()).isEqualTo(1L);
            verify(carritoRepository, never()).save(any());
        }

        @Test
        @DisplayName("crea y persiste un nuevo carrito si el cliente no tiene uno activo")
        void creaCarritoNuevoSiNoExiste() {
            when(carritoRepository.findByClienteIdAndActivoTrue(20L))
                    .thenReturn(Optional.empty());
            when(carritoRepository.findFirstByClienteIdAndCerradoFalseOrderByIdDesc(20L))
                    .thenReturn(Optional.empty());
            when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> {
                Carrito c = i.getArgument(0);
                c.setId(2L);
                return c;
            });

            Carrito resultado = service.obtenerCarritoActivo(20L);

            assertThat(resultado.getClienteId()).isEqualTo(20L);
            verify(carritoRepository).save(any(Carrito.class));
        }
    }

    @Nested
    @DisplayName("anadirProducto")
    class AnadirProducto {

        @Test
        @DisplayName("agrega nuevo item con precio del catalogo")
        void agregaItemNuevoConPrecioDelCatalogo() {
            Carrito carrito = carritoActivo(1L, 10L);
            ProductoClienteDTO prod = producto(100L, 4990.0);

            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(catalogoClient.obtenerProducto(100L)).thenReturn(prod);
            when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 100L))
                    .thenReturn(Optional.empty());
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Carrito resultado = service.anadirProducto(10L, 100L, 2);

            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getProductoId()).isEqualTo(100L);
            assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(2);
            assertThat(resultado.getItems().get(0).getPrecioUnitarioAgregado()).isEqualTo(4990.0);
        }

        @Test
        @DisplayName("incrementa cantidad si el producto ya existe en el carrito")
        void incrementaCantidadSiProductoYaEstaEnCarrito() {
            Carrito carrito = carritoActivo(1L, 10L);
            ItemCarrito itemExistente = item(5L, 1L, 100L, 1, 4990.0);
            carrito.getItems().add(itemExistente);
            ProductoClienteDTO prod = producto(100L, 4990.0);

            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(catalogoClient.obtenerProducto(100L)).thenReturn(prod);
            when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 100L))
                    .thenReturn(Optional.of(itemExistente));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Carrito resultado = service.anadirProducto(10L, 100L, 3);

            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(4);
        }

        @Test
        @DisplayName("lanza RuntimeException si el producto no existe en el catalogo")
        void productoSinRetornoLanzaExcepcion() {
            when(catalogoClient.obtenerProducto(999L))
                    .thenThrow(new RuntimeException("Producto no encontrado en catalogo: 999"));

            assertThatThrownBy(() -> service.anadirProducto(10L, 999L, 1))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("removerProducto")
    class RemoverProducto {

        @Test
        @DisplayName("elimina el item de la coleccion y persiste el carrito")
        void removerItemExitosamente() {
            Carrito carrito = carritoActivo(1L, 10L);
            ItemCarrito item1 = item(5L, 1L, 100L, 2, 4990.0);
            item1.setPosicion(1);
            carrito.getItems().add(item1);

            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Carrito resultado = service.removerProducto(10L, 5L);

            assertThat(resultado.getItems()).isEmpty();
            assertThat(resultado.getFechaUltimaModificacion()).isNotNull();
        }

        @Test
        @DisplayName("recalcula posiciones al remover item intermedio y mantiene activo")
        void removerItemIntermedioRecalculaPosiciones() {
            Carrito carrito = carritoActivo(1L, 10L);
            ItemCarrito item1 = item(5L, 1L, 100L, 2, 4990.0);
            item1.setPosicion(1);
            ItemCarrito item2 = item(6L, 1L, 101L, 1, 2990.0);
            item2.setPosicion(2);
            ItemCarrito item3 = item(7L, 1L, 102L, 3, 1990.0);
            item3.setPosicion(3);
            carrito.getItems().add(item1);
            carrito.getItems().add(item2);
            carrito.getItems().add(item3);

            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Carrito resultado = service.removerProducto(10L, 6L);

            assertThat(resultado.getItems()).hasSize(2);
            assertThat(resultado.getItems().get(0).getId()).isEqualTo(5L);
            assertThat(resultado.getItems().get(0).getPosicion()).isEqualTo(1);
            assertThat(resultado.getItems().get(1).getId()).isEqualTo(7L);
            assertThat(resultado.getItems().get(1).getPosicion()).isEqualTo(2);
            assertThat(resultado.getActivo()).isTrue();
        }

        @Test
        @DisplayName("lanza excepcion si el item no existe en el carrito")
        void removerItemNoEncontrado() {
            Carrito carrito = carritoActivo(1L, 10L);
            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));

            assertThatThrownBy(() -> service.removerProducto(10L, 999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Item no encontrado");
        }
    }

    @Nested
    @DisplayName("seleccionarMetodoPago")
    class SeleccionarMetodoPago {

        @Test
        @DisplayName("persiste el metodoPagoId seleccionado en el carrito")
        void seleccionaMetodoPagoExitosamente() {
            Carrito carrito = carritoActivo(1L, 10L);
            when(procesoPagoClient.validarMetodoPago(3L)).thenReturn(new MetodoPagoDTO(3L, "Tarjeta"));
            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Carrito resultado = service.seleccionarMetodoPago(10L, 3L);

            assertThat(resultado.getMetodoPagoId()).isEqualTo(3L);
        }
    }

    @Nested
    @DisplayName("seleccionarEnvio")
    class SeleccionarEnvio {

        @Test
        @DisplayName("persiste el metodoEnvioId seleccionado en el carrito")
        void seleccionaEnvioExitosamente() {
            Carrito carrito = carritoActivo(1L, 10L);
            when(logisticaEnvioClient.validarMetodoEnvio(2L)).thenReturn(new MetodoEnvioDTO(2L, "Express"));
            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Carrito resultado = service.seleccionarEnvio(10L, 2L);

            assertThat(resultado.getMetodoEnvioId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("vaciarCarrito")
    class VaciarCarrito {

        @Test
        @DisplayName("limpia items, desactiva y persiste el carrito")
        void vaciaCarrito() {
            Carrito carrito = carritoActivo(1L, 10L);
            carrito.getItems().add(item(1L, 1L, 100L, 2, 4990.0));

            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            boolean resultado = service.vaciarCarrito(10L);

            assertThat(resultado).isTrue();
            assertThat(carrito.getItems()).isEmpty();
            assertThat(carrito.getActivo()).isFalse();
        }
    }

    @Nested
    @DisplayName("iniciarProcesoCompra")
    class IniciarProcesoCompra {

        @Test
        @DisplayName("retorna el id del carrito activo")
        void retornaIdCarrito() {
            Carrito carrito = carritoActivo(1L, 10L);

            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));

            Long carritoId = service.iniciarProcesoCompra(10L);

            assertThat(carritoId).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("delega en findAll y retorna todos los carritos")
        void listaAllCarritos() {
            List<Carrito> carritos = List.of(
                    carritoActivo(1L, 10L),
                    carritoActivo(2L, 20L)
            );
            when(carritoRepository.findAll()).thenReturn(carritos);

            List<Carrito> resultado = service.listarTodos();

            assertThat(resultado).hasSize(2);
            verify(carritoRepository).findAll();
        }
    }

    @Nested
    @DisplayName("cerrarCarrito")
    class CerrarCarrito {

        @Test
        @DisplayName("desactiva y marca como cerrado definitivamente el carrito")
        void cerrarCarritoExitosamente() {
            Carrito carrito = carritoActivo(1L, 10L);
            when(carritoRepository.findByClienteIdAndActivoTrue(10L)).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            service.cerrarCarrito(10L);

            assertThat(carrito.getActivo()).isFalse();
            assertThat(carrito.getCerrado()).isTrue();
            verify(carritoRepository).save(carrito);
        }
    }
}
