package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.dto.CarritoDTO;
import com.ecomarket.pedidos.dto.ItemCarritoDTO;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoServiceImpl")
class PedidoServiceImplTest {

    @Mock PedidoRepository pedidoRepository;
    @Mock ItemPedidoRepository itemPedidoRepository;
    @Mock EstadoPedidoRepository estadoPedidoRepository;
    @Mock RestTemplate restTemplate;

    @InjectMocks PedidoServiceImpl service;

    private EstadoPedido estado(String nombre) {
        EstadoPedido e = new EstadoPedido();
        e.setId(1L);
        e.setNombre(nombre);
        return e;
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
                .items(List.of(item))
                .build();
    }

    private Pedido pedidoPendiente(Long id) {
        return Pedido.builder()
                .id(id)
                .clienteId(5L)
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
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(carrito);
            when(estadoPedidoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            when(itemPedidoRepository.saveAll(any())).thenReturn(List.of());

            Pedido resultado = service.generarPedidoDesdeCarrito(5L, 1L);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getClienteId()).isEqualTo(5L);
            assertThat(resultado.getSubtotal()).isEqualTo(50000.0);
            assertThat(resultado.getEstado().getNombre()).isEqualTo("PENDIENTE");
            verify(restTemplate).getForObject(anyString(), eq(CarritoDTO.class));
            verify(restTemplate).delete(anyString());
            verify(pedidoRepository).save(any());
            verify(itemPedidoRepository).saveAll(any());
        }

        @Test
        @DisplayName("lanza excepción si el carrito está vacío")
        void carritoVacio() {
            CarritoDTO vacio = CarritoDTO.builder().items(List.of()).build();
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(vacio);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("vacío");
        }

        @Test
        @DisplayName("lanza excepción si el carrito es nulo")
        void carritoNulo() {
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(null);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("vacío");
        }

        @Test
        @DisplayName("lanza excepción si los items del carrito son nulos")
        void carritoItemsNulos() {
            CarritoDTO carrito = CarritoDTO.builder().items(null).build();
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(carrito);

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("vacío");
        }

        @Test
        @DisplayName("lanza excepción si estado PENDIENTE no existe")
        void estadoPendienteNoExiste() {
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(carritoDto());
            when(estadoPedidoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("PENDIENTE");
        }

        @Test
        @DisplayName("tolera fallo al vaciar carrito externo")
        void toleraFalloAlVaciarCarrito() {
            CarritoDTO carrito = carritoDto();
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(carrito);
            when(estadoPedidoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            doThrow(new RuntimeException("Connection refused")).when(restTemplate).delete(anyString());

            assertThatNoException().isThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 1L));
        }

        @Test
        @DisplayName("tolera fallo al enviar log de analítica")
        void toleraFalloAlEnviarLog() {
            CarritoDTO carrito = carritoDto();
            when(restTemplate.getForObject(anyString(), eq(CarritoDTO.class))).thenReturn(carrito);
            when(estadoPedidoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(pedidoRepository.save(any())).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            when(itemPedidoRepository.saveAll(any())).thenReturn(List.of());
            doThrow(new RuntimeException("Log service down")).when(restTemplate).postForEntity(anyString(), any(), eq(String.class));

            assertThatNoException().isThrownBy(() -> service.generarPedidoDesdeCarrito(5L, 1L));
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
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si el estado no existe")
        void estadoNoExiste() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente(1L)));
            when(estadoPedidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstado(1L, 99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Estado no encontrado");
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
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }
    }
}
