package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.client.AnaliticaClient;
import com.ecomarket.pedidos.client.CarritoCompraClient;
import com.ecomarket.pedidos.client.CatalogoInventarioClient;
import com.ecomarket.pedidos.client.RegistroUsuariosClient;
import com.ecomarket.pedidos.dto.CarritoDTO;
import com.ecomarket.pedidos.dto.ItemCarritoDTO;
import com.ecomarket.pedidos.dto.PerfilUsuarioDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;
import com.ecomarket.pedidos.model.*;
import com.ecomarket.pedidos.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService")
class PedidoServiceTest {

    @Mock PedidoRepository pedidoRepository;
    @Mock ItemPedidoRepository itemPedidoRepository;
    @Mock EstadoPedidoRepository estadoPedidoRepository;
    @Mock RegistroUsuariosClient registroUsuariosClient;
    @Mock CarritoCompraClient carritoCompraClient;
    @Mock CatalogoInventarioClient catalogoInventarioClient;
    @Mock AnaliticaClient analiticaClient;
    @Mock RestTemplate restTemplate;

    @InjectMocks PedidoService service;

    private EstadoPedido estado(String nombre) {
        return EstadoPedido.builder().id(1L).nombre(nombre).build();
    }

    private CarritoDTO carritoDto() {
        ItemCarritoDTO item = ItemCarritoDTO.builder()
                .productoId(100L)
                .cantidad(2)
                .precioUnitarioAgregado(25000.0)
                .build();
        return CarritoDTO.builder()
                .id(1L)
                .clienteId(5L)
                .subtotal(50000.0)
                .metodoPagoId(1L)
                .metodoEnvioId(1L)
                .items(List.of(item))
                .build();
    }

    private Pedido pedidoPendiente(Long id) {
        return Pedido.builder()
                .id(id)
                .clienteId(5L)
                .carritoId(1L)
                .direccionEnvioId(100L)
                .subtotal(50000.0)
                .total(50000.0)
                .estado(estado("PENDIENTE"))
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("generarPedidoDesdeCarrito")
    class GenerarPedido {

        @Test
        @DisplayName("crea pedido y items desde el carrito externo")
        void generaPedidoExitoso() {
            CarritoDTO carrito = carritoDto();
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carrito);
            when(catalogoInventarioClient.obtenerProducto(100L)).thenReturn(null);
            when(estadoPedidoRepository.findById(1L)).thenReturn(Optional.of(estado("PENDIENTE")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });

            Pedido resultado = service.generarPedidoDesdeCarrito(5L, 100L);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getClienteId()).isEqualTo(5L);
            assertThat(resultado.getCarritoId()).isEqualTo(1L);
            assertThat(resultado.getDireccionEnvioId()).isEqualTo(100L);
            assertThat(resultado.getSubtotal()).isEqualTo(50000.0);
            assertThat(resultado.getEstado().getNombre()).isEqualTo("PENDIENTE");
            verify(registroUsuariosClient).obtenerUsuario(5L);
            verify(carritoCompraClient).obtenerCarrito(5L);
            verify(catalogoInventarioClient).obtenerProducto(100L);
            verify(estadoPedidoRepository).findById(1L);
            verify(pedidoRepository).save(any());
            verify(itemPedidoRepository).saveAll(any());
            verify(carritoCompraClient).cerrarCarrito(5L);
            verify(carritoCompraClient).vaciarCarrito(5L);
            verify(analiticaClient).registrarLog(any());
        }

        @Test
        @DisplayName("lanza excepción si el carrito está vacío")
        void carritoVacio() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            CarritoDTO vacio = CarritoDTO.builder().items(List.of()).build();
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(vacio);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 100L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("vacío");
        }

        @Test
        @DisplayName("lanza excepción si el carrito es nulo")
        void carritoNulo() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(null);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 100L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("vacío");
        }

        @Test
        @DisplayName("lanza excepción si los items del carrito son nulos")
        void carritoItemsNulos() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            CarritoDTO carrito = CarritoDTO.builder().items(null).build();
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carrito);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 100L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("vacío");
        }

        @Test
        @DisplayName("lanza excepción si el carrito no tiene método de pago")
        void carritoSinMetodoPago() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            CarritoDTO sinPago = CarritoDTO.builder()
                    .id(1L)
                    .clienteId(5L)
                    .items(List.of(ItemCarritoDTO.builder().productoId(100L).cantidad(1).precioUnitarioAgregado(1000.0).build()))
                    .metodoEnvioId(1L)
                    .build();
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(sinPago);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 100L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("método de pago");
        }

        @Test
        @DisplayName("lanza excepción si el carrito no tiene método de envío")
        void carritoSinMetodoEnvio() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            CarritoDTO sinEnvio = CarritoDTO.builder()
                    .id(1L)
                    .clienteId(5L)
                    .items(List.of(ItemCarritoDTO.builder().productoId(100L).cantidad(1).precioUnitarioAgregado(1000.0).build()))
                    .metodoPagoId(1L)
                    .build();
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(sinEnvio);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 100L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("método de envío");
        }

        @Test
        @DisplayName("lanza excepción si estado inicial no existe")
        void estadoInicialNoExiste() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carritoDto());
            when(catalogoInventarioClient.obtenerProducto(100L)).thenReturn(null);
            when(estadoPedidoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 100L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("estado inicial");
        }

        @Test
        @DisplayName("obtiene direccion predeterminada si no se envía direccionEnvioId")
        void direccionDesdeApiCuandoIdEsNull() {
            CarritoDTO carrito = carritoDto();
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carrito);
            when(catalogoInventarioClient.obtenerProducto(100L)).thenReturn(null);
            when(estadoPedidoRepository.findById(1L)).thenReturn(Optional.of(estado("PENDIENTE")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(Map.of("id", 200));

            Pedido resultado = service.generarPedidoDesdeCarrito(5L, null);

            assertThat(resultado.getDireccionEnvioId()).isEqualTo(200L);
            verify(restTemplate).getForObject(anyString(), eq(Map.class));
        }

        @Test
        @DisplayName("lanza excepción si la dirección predeterminada no tiene id")
        void direccionPredeterminadaSinId() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carritoDto());
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(Map.of());

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, null))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("dirección predeterminada");
        }

        @Test
        @DisplayName("lanza excepción si falla la obtención de dirección predeterminada")
        void falloAlObtenerDireccionPredeterminada() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carritoDto());
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenThrow(new RuntimeException("Timeout"));

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, null))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("dirección predeterminada");
        }

        @Test
        @DisplayName("lanza excepción si la API de direcciones retorna null")
        void direccionApiRetornaNull() {
            when(registroUsuariosClient.obtenerUsuario(5L)).thenReturn(new PerfilUsuarioDTO());
            when(carritoCompraClient.obtenerCarrito(5L)).thenReturn(carritoDto());
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(null);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, null))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("dirección predeterminada");
        }
    }

    @Nested
    @DisplayName("actualizarEstado")
    class ActualizarEstado {

        @Test
        @DisplayName("cambia el estado del pedido")
        void actualizaEstado() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));
            when(estadoPedidoRepository.findById(2L)).thenReturn(Optional.of(estado("CONFIRMADO")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Pedido resultado = service.actualizarEstado(1L, 2L);

            assertThat(resultado.getEstado().getNombre()).isEqualTo("CONFIRMADO");
        }

        @Test
        @DisplayName("lanza excepción si el pedido no existe")
        void pedidoNoExiste() {
            when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstado(99L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si el estado no existe")
        void estadoNoExiste() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));
            when(estadoPedidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstado(1L, 99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("Estado de pedido no encontrado");
        }

        @Test
        @DisplayName("dispara creación de envío cuando estadoId es 4")
        void disparaCreacionEnvio() {
            Pedido pedido = pedidoPendiente(1L);
            pedido.setDireccionEnvioId(100L);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(estadoPedidoRepository.findById(4L)).thenReturn(Optional.of(estado("ENVIADO")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.actualizarEstado(1L, 4L);

            verify(restTemplate).postForEntity(contains("/envios/auto/1"), isNull(), eq(String.class));
        }

        @Test
        @DisplayName("tolera error al disparar creación de envío")
        void toleraErrorAlDispararEnvio() {
            Pedido pedido = pedidoPendiente(1L);
            pedido.setDireccionEnvioId(100L);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(estadoPedidoRepository.findById(4L)).thenReturn(Optional.of(estado("ENVIADO")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            doThrow(new RuntimeException("Logistics down")).when(restTemplate).postForEntity(anyString(), any(), eq(String.class));

            assertThatNoException().isThrownBy(() -> service.actualizarEstado(1L, 4L));
        }
    }

    @Nested
    @DisplayName("actualizarEstadoPorNombre")
    class ActualizarEstadoPorNombre {

        @Test
        @DisplayName("cambia el estado del pedido por nombre")
        void actualizaPorNombre() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));
            when(estadoPedidoRepository.findByNombre("CONFIRMADO")).thenReturn(Optional.of(estado("CONFIRMADO")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Pedido resultado = service.actualizarEstadoPorNombre(1L, "CONFIRMADO");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("CONFIRMADO");
        }

        @Test
        @DisplayName("lanza excepción si el pedido no existe")
        void pedidoNoExiste() {
            when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstadoPorNombre(99L, "CONFIRMADO"))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si el nombre de estado no existe")
        void nombreEstadoNoExiste() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));
            when(estadoPedidoRepository.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstadoPorNombre(1L, "INEXISTENTE"))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("Estado de pedido no encontrado con nombre");
        }

        @Test
        @DisplayName("dispara creación de envío cuando nombre es ENVIADO")
        void disparaCreacionEnvio() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));
            when(estadoPedidoRepository.findByNombre("ENVIADO")).thenReturn(Optional.of(estado("ENVIADO")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.actualizarEstadoPorNombre(1L, "ENVIADO");

            verify(restTemplate).postForEntity(contains("/envios/auto/1"), isNull(), eq(String.class));
        }
    }

    @Nested
    @DisplayName("obtenerHistorialCliente")
    class ObtenerHistorial {

        @Test
        @DisplayName("retorna lista de pedidos del cliente")
        void retornaHistorial() {
            when(pedidoRepository.findByClienteId(5L)).thenReturn(List.of(pedidoPendiente(1L)));

            List<Pedido> resultado = service.obtenerHistorialCliente(5L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getClienteId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("retorna lista vacía si no hay pedidos")
        void sinPedidos() {
            when(pedidoRepository.findByClienteId(99L)).thenReturn(List.of());

            List<Pedido> resultado = service.obtenerHistorialCliente(99L);

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("retorna pedido cuando existe")
        void retornaPedido() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));

            Pedido resultado = service.buscarPorId(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("lanza excepción si el pedido no existe")
        void pedidoNoExiste() {
            when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }
}
