package com.ecomarket.procesopagoservice.service;

import com.ecomarket.procesopagoservice.exception.*;
import com.ecomarket.procesopagoservice.model.*;
import com.ecomarket.procesopagoservice.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService")
class PagoServiceTest {

    @Mock TransaccionRepository transaccionRepository;
    @Mock FacturaRepository     facturaRepository;
    @Mock CuponRepository       cuponRepository;
    @Mock EstadoPagoRepository  estadoPagoRepository;
    @Mock MetodoPagoRepository  metodoPagoRepository;
    @Mock RestTemplate          restTemplate;

    @InjectMocks PagoService service;

    private EstadoPago estado(String nombre) {
        EstadoPago e = new EstadoPago();
        e.setId(1L);
        e.setNombre(nombre);
        return e;
    }

    private MetodoPagoTransaccion metodo() {
        MetodoPagoTransaccion m = new MetodoPagoTransaccion();
        m.setId(1L);
        m.setNombre("TARJETA_CREDITO");
        return m;
    }

    private MetodoPagoTransaccion metodoManual() {
        MetodoPagoTransaccion m = new MetodoPagoTransaccion();
        m.setId(2L);
        m.setNombre("Transferencia Bancaria");
        return m;
    }

    private TransaccionPago transaccion(Long id, Double subtotal) {
        TransaccionPago t = new TransaccionPago();
        t.setId(id);
        t.setPedidoId(10L);
        t.setClienteId(5L);
        t.setMontoSubtotal(subtotal);
        t.setMontoDescuento(0.0);
        t.setMontoTotal(subtotal);
        t.setMetodoPago(metodo());
        t.setEstado(estado("PENDIENTE"));
        return t;
    }

    private TransaccionPago transaccionCompleta(Long id) {
        TransaccionPago t = transaccion(id, 50000.0);
        t.setIdempotencyKey("idem-123");
        t.setFechaInicio(LocalDateTime.now());
        t.setFechaUltimaActualizacion(LocalDateTime.now());
        t.setTokenTransbank("TB-ABCD1234");
        return t;
    }

    private CuponDescuento cuponValido(Double porcentaje, Double maximo) {
        CuponDescuento c = new CuponDescuento();
        c.setId(1L);
        c.setCodigo("DESC10");
        c.setPorcentajeDescuento(porcentaje);
        c.setMontoMaximoDescuento(maximo);
        c.setFechaExpiracion(LocalDateTime.now().plusDays(10));
        c.setActivo(true);
        return c;
    }

    private CuponDescuento crearCuponExpirado() {
        CuponDescuento c = new CuponDescuento();
        c.setId(2L);
        c.setCodigo("VIEJOCUPON");
        c.setPorcentajeDescuento(10.0);
        c.setFechaExpiracion(LocalDateTime.now().minusDays(1));
        c.setActivo(true);
        return c;
    }

    private Map<String, Object> pedidoData() {
        Map<String, Object> data = new HashMap<>();
        data.put("clienteId", 5);
        data.put("total", 50000.0);
        data.put("metodoPagoId", 1);
        Map<String, Object> estado = new HashMap<>();
        estado.put("nombre", "PENDIENTE");
        data.put("estado", estado);
        return data;
    }

    @Nested
    @DisplayName("iniciarPago")
    class IniciarPago {

        private void setupHappyPathMocks() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(pedidoData());
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(estadoPagoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(transaccionRepository.save(any(TransaccionPago.class))).thenAnswer(inv -> {
                TransaccionPago t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });
        }

        @Test
        @DisplayName("crea transacción y retorna con estado PENDIENTE")
        void creaTransaccionExitosa() {
            setupHappyPathMocks();

            TransaccionPago resultado = service.iniciarPago(10L, "idem-456");

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getPedidoId()).isEqualTo(10L);
            assertThat(resultado.getClienteId()).isEqualTo(5L);
            assertThat(resultado.getMontoTotal()).isEqualTo(50000.0);
            assertThat(resultado.getMetodoPago().getId()).isEqualTo(1L);
            assertThat(resultado.getEstado().getNombre()).isEqualTo("PENDIENTE");
            assertThat(resultado.getIdempotencyKey()).isEqualTo("idem-456");
            verify(transaccionRepository).save(any());
        }

        @Test
        @DisplayName("idempotencyKey vacío → no busca duplicado y genera UUID")
        void idempotencyKeyVacio() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(pedidoData());
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(estadoPagoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> {
                TransaccionPago t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });

            TransaccionPago resultado = service.iniciarPago(10L, "");

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getIdempotencyKey()).isNotNull();
            verify(transaccionRepository, never()).findByIdempotencyKey(any());
        }

        @Test
        @DisplayName("retorna transacción existente si idempotencyKey coincide")
        void retornaExistentePorIdempotencyKey() {
            TransaccionPago existente = transaccionCompleta(1L);
            when(transaccionRepository.findByIdempotencyKey("idem-dup")).thenReturn(Optional.of(existente));

            TransaccionPago resultado = service.iniciarPago(10L, "idem-dup");

            assertThat(resultado.getId()).isEqualTo(1L);
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("idempotencyKey nulo no busca duplicado y genera UUID")
        void idempotencyKeyNulo() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(pedidoData());
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(estadoPagoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> {
                TransaccionPago t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });

            TransaccionPago resultado = service.iniciarPago(10L, null);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getIdempotencyKey()).isNotNull();
            verify(transaccionRepository, never()).findByIdempotencyKey(any());
        }

        @Test
        @DisplayName("lanza excepción si el pedido no tiene método de pago asignado")
        void pedidoSinMetodoPago() {
            Map<String, Object> data = new HashMap<>(pedidoData());
            data.remove("metodoPagoId");
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(data);

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("método de pago asignado");
        }

        @Test
        @DisplayName("lanza excepción si método de pago no existe")
        void metodoPagoNoExiste() {
            Map<String, Object> data = pedidoData();
            data.put("metodoPagoId", 99);
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(data);
            when(metodoPagoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si pedido no se encuentra (restTemplate retorna null)")
        void pedidoDataNulo() {
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Pedido no encontrado");
        }

        @Test
        @DisplayName("lanza excepción si pedido ya está confirmado (estado CONFIRMADO)")
        void pedidoYaConfirmado() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            Map<String, Object> data = pedidoData();
            Map<String, Object> estado = new HashMap<>();
            estado.put("nombre", "CONFIRMADO");
            data.put("estado", estado);
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(data);

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(EstadoTransaccionInvalidoException.class)
                    .hasMessageContaining("procesado o enviado");
        }

        @Test
        @DisplayName("lanza excepción si pedido ya está enviado (estado ENVIADO)")
        void pedidoYaEnviado() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of(
                    "clienteId", 5, "total", 50000.0, "metodoPagoId", 1, "estado", "ENVIADO"
            ));

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(EstadoTransaccionInvalidoException.class)
                    .hasMessageContaining("procesado o enviado");
        }

        @Test
        @DisplayName("lanza Http 404 al consultar pedido (NotFound)")
        void pedidoHttp404() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenThrow(mock(HttpClientErrorException.NotFound.class));

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("no existe en el servicio de pedidos");
        }

        @Test
        @DisplayName("lanza RecursoNoEncontradoException si falla la comunicación con pedidos")
        void pedidoComunicacionFallida() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenThrow(new RuntimeException("Connection refused"));

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Error interno");
        }

        @Test
        @DisplayName("retorna transacción activa existente si hay una en estado no terminal")
        void retornaTransaccionActivaExistente() {
            TransaccionPago activa = transaccion(2L, 50000.0);
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(pedidoData());
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(transaccionRepository.findFirstByPedidoIdAndEstadoNotIn(eq(10L), anyList())).thenReturn(Optional.of(activa));

            TransaccionPago resultado = service.iniciarPago(10L, "idem");

            assertThat(resultado.getId()).isEqualTo(2L);
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("estado como tipo desconocido → continúa como DESCONOCIDO")
        void pedidoEstadoTipoDesconocido() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of(
                    "clienteId", 5, "total", 50000.0, "metodoPagoId", 1, "estado", 999
            ));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(estadoPagoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado("PENDIENTE")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> {
                TransaccionPago t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });

            TransaccionPago resultado = service.iniciarPago(10L, "idem");

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("lanza excepción si no hay estado inicial disponible")
        void estadoInicialNoDisponible() {
            when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodo()));
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(pedidoData());
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(estadoPagoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.iniciarPago(10L, "idem"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("estado inicial");
        }
    }

    @Nested
    @DisplayName("anadirCuponDescuento")
    class AnadirCupon {

        @Test
        @DisplayName("aplica descuento porcentual correctamente")
        void aplicaDescuentoPorcentual() {
            TransaccionPago t = transaccion(1L, 100000.0);
            CuponDescuento c = cuponValido(10.0, null);

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(cuponRepository.findById(1L)).thenReturn(Optional.of(c));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.anadirCuponDescuento(1L, 1L);

            assertThat(resultado.getMontoDescuento()).isEqualTo(10000.0);
            assertThat(resultado.getMontoTotal()).isEqualTo(90000.0);
            assertThat(resultado.getCuponUtilizadoId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("respeta el techo de montoMaximoDescuento")
        void respetaTechoDescuento() {
            TransaccionPago t = transaccion(1L, 200000.0);
            CuponDescuento c = cuponValido(20.0, 15000.0);

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(cuponRepository.findById(1L)).thenReturn(Optional.of(c));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.anadirCuponDescuento(1L, 1L);

            assertThat(resultado.getMontoDescuento()).isEqualTo(15000.0);
            assertThat(resultado.getMontoTotal()).isEqualTo(185000.0);
        }

        @Test
        @DisplayName("no sobrepasa el techo si el descuento no excede el máximo")
        void descuentoNoExcedeTecho() {
            TransaccionPago t = transaccion(1L, 50000.0);
            CuponDescuento c = cuponValido(5.0, 10000.0);

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(cuponRepository.findById(1L)).thenReturn(Optional.of(c));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.anadirCuponDescuento(1L, 1L);

            assertThat(resultado.getMontoDescuento()).isEqualTo(2500.0);
            assertThat(resultado.getMontoTotal()).isEqualTo(47500.0);
        }

        @Test
        @DisplayName("lanza excepción si transacción está en estado APROBADO")
        void transaccionEnEstadoAprobado() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setEstado(estado("APROBADO"));
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 1L))
                    .isInstanceOf(EstadoTransaccionInvalidoException.class)
                    .hasMessageContaining("No se puede aplicar cupón");

            verify(cuponRepository, never()).findById(any());
        }

        @Test
        @DisplayName("lanza excepción si transacción está en estado RECHAZADO")
        void transaccionEnEstadoRechazado() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setEstado(estado("RECHAZADO"));
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 1L))
                    .isInstanceOf(EstadoTransaccionInvalidoException.class)
                    .hasMessageContaining("No se puede aplicar cupón");

            verify(cuponRepository, never()).findById(any());
        }

        @Test
        @DisplayName("lanza excepción si transacción está en estado REEMBOLSADO")
        void transaccionEnEstadoReembolsado() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setEstado(estado("REEMBOLSADO"));
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 1L))
                    .isInstanceOf(EstadoTransaccionInvalidoException.class)
                    .hasMessageContaining("No se puede aplicar cupón");

            verify(cuponRepository, never()).findById(any());
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.anadirCuponDescuento(99L, 1L))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");

            verify(cuponRepository, never()).findById(any());
        }

        @Test
        @DisplayName("lanza excepción si cupón no existe")
        void cuponNoExiste() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 99L))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si cupón está expirado")
        void cuponExpirado() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            when(cuponRepository.findById(2L)).thenReturn(Optional.of(crearCuponExpirado()));

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 2L))
                    .isInstanceOf(CuponInvalidoException.class)
                    .hasMessageContaining("no es válido");
        }

        @Test
        @DisplayName("lanza excepción si cupón está inactivo")
        void cuponInactivo() {
            CuponDescuento inactivo = cuponValido(10.0, null);
            inactivo.setActivo(false);

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            when(cuponRepository.findById(1L)).thenReturn(Optional.of(inactivo));

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 1L))
                    .isInstanceOf(CuponInvalidoException.class)
                    .hasMessageContaining("no es válido");
        }
    }

    @Nested
    @DisplayName("procesarConTransbank")
    class ProcesarTransbank {

        private void setupMockParaProcesar() {
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        }

        @Test
        @DisplayName("aprueba el pago y guarda código de autorización")
        void apruebaPago() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            setupMockParaProcesar();

            TransaccionPago resultado = service.procesarConTransbank(1L, "TOKEN-ABC-123");

            assertThat(resultado.getCodigoAutorizacion()).isNotNull().isNotEmpty();
            assertThat(resultado.getEstado().getNombre()).isEqualTo("APROBADO");
            verify(transaccionRepository, atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarConTransbank(99L, "TOKEN"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("token nulo → estado RECHAZADO")
        void tokenNulo() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, null);

            assertThat(resultado.getEstado().getNombre()).isEqualTo("RECHAZADO");
            assertThat(resultado.getMensajeError()).contains("Token de pago inválido");
        }

        @Test
        @DisplayName("token vacío → estado RECHAZADO")
        void tokenVacio() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, "");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("RECHAZADO");
            assertThat(resultado.getMensajeError()).contains("Token de pago inválido");
        }

        @Test
        @DisplayName("método manual (Transferencia Bancaria) → no actualiza estado del pedido")
        void metodoManualNoActualizaPedido() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setMetodoPago(metodoManual());
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, "TOKEN-VALIDO");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("APROBADO");
            verify(restTemplate, never()).put(anyString(), any());
        }

        @Test
        @DisplayName("token 'error' → estado RECHAZADO")
        void tokenError() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, "error");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("RECHAZADO");
        }

        @Test
        @DisplayName("token inválido + RECHAZADO no encontrado → lanza excepción")
        void tokenErrorConRechazadoNoDisponible() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarConTransbank(1L, "error"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("RECHAZADO");
        }

        @Test
        @DisplayName("error genérico + RECHAZADO no encontrado → lanza excepción")
        void errorGenericoConRechazadoNoDisponible() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenThrow(new RuntimeException("BD caída"));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("RECHAZADO");
        }

        @Test
        @DisplayName("error genérico en procesamiento → estado RECHAZADO")
        void errorGenerico() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenThrow(new RuntimeException("BD caída"));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, "TOKEN");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("RECHAZADO");
            assertThat(resultado.getMensajeError()).contains("Error interno");
        }

        @Test
        @DisplayName("tolera fallo al actualizar pedido vía PUT")
        void toleraFalloAlActualizarPedido() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            doThrow(new RuntimeException("Pedidos service down")).when(restTemplate).put(anyString(), any());
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertThatNoException().isThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"));
        }

        @Test
        @DisplayName("tolera fallo al vaciar carrito")
        void toleraFalloDeCarrito() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            doThrow(new RuntimeException("Carrito down")).when(restTemplate).delete(anyString());
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertThatNoException().isThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"));
        }

        @Test
        @DisplayName("tolera fallo al enviar log de analítica")
        void toleraFalloAlEnviarLog() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.of(estado("APROBADO")));
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new RuntimeException("Analítica caída"));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertThatNoException().isThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"));
        }

        @Test
        @DisplayName("APROBADO no encontrado → cae a RECHAZADO")
        void estadoAprobadoNoEncontrado() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("RECHAZADO")).thenReturn(Optional.of(estado("RECHAZADO")));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.empty());
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, "TOKEN");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("RECHAZADO");
        }
    }

    @Nested
    @DisplayName("procesarReembolso")
    class ProcesarReembolso {

        @Test
        @DisplayName("cambia estado a REEMBOLSADO y retorna true")
        void reembolsoExitoso() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setEstado(estado("APROBADO"));

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO"))
                    .thenReturn(Optional.of(estado("REEMBOLSADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Boolean resultado = service.procesarReembolso(1L, "Producto dañado");

            assertThat(resultado).isTrue();
            assertThat(t.getEstado().getNombre()).isEqualTo("REEMBOLSADO");
            verify(transaccionRepository).save(t);
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarReembolso(99L, "Motivo"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si transacción no está APROBADA")
        void transaccionNoAprobada() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setEstado(estado("PENDIENTE"));
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));

            assertThatThrownBy(() -> service.procesarReembolso(1L, "Motivo"))
                    .isInstanceOf(EstadoTransaccionInvalidoException.class)
                    .hasMessageContaining("Solo se pueden reembolsar");
        }

        @Test
        @DisplayName("lanza excepción si estado REEMBOLSADO no existe en BD")
        void estadoReembolsadoNoExiste() {
            TransaccionPago t = transaccion(1L, 50000.0);
            t.setEstado(estado("APROBADO"));
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarReembolso(1L, "Motivo"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("REEMBOLSADO");
        }
    }

    @Nested
    @DisplayName("generarFactura")
    class GenerarFactura {

        @Test
        @DisplayName("genera factura con datos de la transacción y del receptor")
        void generaFacturaExitosa() {
            TransaccionPago t = transaccion(1L, 80000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(facturaRepository.save(any(FacturaElectronica.class)))
                    .thenAnswer(inv -> {
                        FacturaElectronica f = inv.getArgument(0);
                        f.setId(1L);
                        return f;
                    });

            FacturaElectronica factura = service.generarFactura(1L, 12345678L, "Comercio Electrónico");

            assertThat(factura.getId()).isEqualTo(1L);
            assertThat(factura.getTransaccionId()).isEqualTo(1L);
            assertThat(factura.getClienteId()).isEqualTo(5L);
            assertThat(factura.getRutReceptor()).isEqualTo("12345678");
            assertThat(factura.getRazonSocial()).isEqualTo("Comercio Electrónico");
            assertThat(factura.getFechaEmision()).isNotNull();
            assertThat(factura.getFolioFiscal()).isPositive();
            assertThat(factura.getXmlDocumento()).contains("<factura><transaccion>1</transaccion></factura>");
            verify(facturaRepository).save(any(FacturaElectronica.class));
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.generarFactura(99L, 12345678L, "Giro"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");

            verify(facturaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("enviarBoletaEmail")
    class EnviarBoletaEmail {

        @Test
        @DisplayName("retorna true al enviar boleta a correo válido")
        void envioExitoso() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));

            Boolean resultado = service.enviarBoletaEmail(1L, "cliente@ejemplo.cl");

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("retorna true incluso con correo vacío")
        void envioConCorreoVacio() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));

            Boolean resultado = service.enviarBoletaEmail(1L, "");

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.enviarBoletaEmail(99L, "correo@test.cl"))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("obtenerTransaccion")
    class ObtenerTransaccion {

        @Test
        @DisplayName("retorna la transacción cuando existe")
        void retornaTransaccion() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));

            TransaccionPago resultado = service.obtenerTransaccion(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getMontoTotal()).isEqualTo(50000.0);
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.obtenerTransaccion(99L))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }
    }
}
