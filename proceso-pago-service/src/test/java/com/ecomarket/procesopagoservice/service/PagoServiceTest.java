package com.ecomarket.procesopagoservice.service;

import com.ecomarket.procesopagoservice.model.*;
import com.ecomarket.procesopagoservice.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PagoService.
 *
 * Ejecutar:
 *   mvn test -pl proceso-pago-service -Dtest=PagoServiceTest
 */
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

    // ── Fixtures ──────────────────────────────────────────────────────────────

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

    private CuponDescuento cuponValido(Double porcentaje, Double maximo) {
        CuponDescuento c = new CuponDescuento();
        c.setId(1L);
        c.setCodigo("DESC10");
        c.setPorcentajeDescuento(BigDecimal.valueOf(porcentaje));
        c.setMontoMaximoDescuento(maximo != null ? BigDecimal.valueOf(maximo) : null);
        c.setFechaExpiracion(LocalDateTime.now().plusDays(10));
        c.setActivo(true);
        return c;
    }

    private CuponDescuento cuponExpirado() {
        CuponDescuento c = new CuponDescuento();
        c.setId(2L);
        c.setCodigo("VIEJOCUPON");
        c.setPorcentajeDescuento(BigDecimal.valueOf(10.0));
        c.setFechaExpiracion(LocalDateTime.now().minusDays(1));
        c.setActivo(true);
        return c;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // iniciarPago
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("iniciarPago")
    class IniciarPago {

        @Test
        @DisplayName("crea transacción PENDIENTE con monto correcto")
        void creaTransaccionExitosa() {
            when(estadoPagoRepository.findByNombre("PENDIENTE"))
                    .thenReturn(Optional.of(estado("PENDIENTE")));
            when(transaccionRepository.save(any(TransaccionPago.class)))
                    .thenAnswer(inv -> {
                        TransaccionPago t = inv.getArgument(0);
                        t.setId(1L);
                        return t;
                    });

            TransaccionPago resultado = service.iniciarPago(10L, 5L, 50000.0, metodo());

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getMontoTotal()).isEqualTo(50000.0);
            assertThat(resultado.getMontoDescuento()).isEqualTo(0.0);
            assertThat(resultado.getEstado().getNombre()).isEqualTo("PENDIENTE");
            assertThat(resultado.getPedidoId()).isEqualTo(10L);
            assertThat(resultado.getClienteId()).isEqualTo(5L);
            verify(transaccionRepository).save(any(TransaccionPago.class));
        }

        @Test
        @DisplayName("lanza excepción si estado PENDIENTE no existe en BD")
        void estadoPendienteNoExiste() {
            when(estadoPagoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.iniciarPago(10L, 5L, 50000.0, metodo()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("PENDIENTE");

            verify(transaccionRepository, never()).save(any());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // anadirCuponDescuento
    // ═════════════════════════════════════════════════════════════════════════

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
            CuponDescuento c = cuponValido(20.0, 15000.0); // 20% = 40000, techo 15000

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(cuponRepository.findById(1L)).thenReturn(Optional.of(c));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.anadirCuponDescuento(1L, 1L);

            assertThat(resultado.getMontoDescuento()).isEqualTo(15000.0);
            assertThat(resultado.getMontoTotal()).isEqualTo(185000.0);
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.anadirCuponDescuento(99L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");

            verify(cuponRepository, never()).findById(any());
        }

        @Test
        @DisplayName("lanza excepción si cupón no existe")
        void cuponNoExiste() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si cupón está expirado")
        void cuponExpirado() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            CuponDescuento cuponExpirado = new CuponDescuento();
            cuponExpirado.setId(2L);
            cuponExpirado.setCodigo("VIEJOCUPON");
            cuponExpirado.setPorcentajeDescuento(new BigDecimal("10.0"));
            cuponExpirado.setFechaExpiracion(LocalDateTime.now().minusDays(1));
            cuponExpirado.setActivo(true);
            when(cuponRepository.findById(2L)).thenReturn(Optional.of(cuponExpirado));

            assertThatThrownBy(() -> service.anadirCuponDescuento(1L, 2L))
                    .isInstanceOf(RuntimeException.class)
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
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no es válido");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // procesarConTransbank
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("procesarConTransbank")
    class ProcesarTransbank {

        @Test
        @DisplayName("aprueba el pago y guarda token + fecha autorización")
        void apruebaPago() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO"))
                    .thenReturn(Optional.of(estado("APROBADO")));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            TransaccionPago resultado = service.procesarConTransbank(1L, "TOKEN-ABC-123");

            assertThat(resultado.getTokenTransbank()).isEqualTo("TOKEN-ABC-123");
            assertThat(resultado.getCodigoAutorizacion()).isNotNull().isNotEmpty();
            assertThat(resultado.getEstado().getNombre()).isEqualTo("APROBADO");
            verify(transaccionRepository).save(any());
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarConTransbank(99L, "TOKEN"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si estado APROBADO no existe en BD")
        void estadoAprobadoNoExiste() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            when(estadoPagoRepository.findByNombre("APROBADO")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("APROBADO");
        }

        @Test
        @DisplayName("continúa aunque el microservicio de pedidos falle (tolerancia a fallos)")
        void toleraFalloDePedidos() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO"))
                    .thenReturn(Optional.of(estado("APROBADO")));
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new RuntimeException("Connection refused"));
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // No debe lanzar excepción pese al fallo externo
            assertThatNoException().isThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"));
        }

        @Test
        @DisplayName("continúa aunque el microservicio de carrito falle al vaciar (tolerancia a fallos)")
        void toleraFalloDeCarrito() {
            TransaccionPago t = transaccion(1L, 50000.0);
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));
            when(estadoPagoRepository.findByNombre("APROBADO"))
                    .thenReturn(Optional.of(estado("APROBADO")));
            doThrow(new RuntimeException("Connection refused"))
                    .when(restTemplate).delete(anyString());
            when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertThatNoException().isThrownBy(() -> service.procesarConTransbank(1L, "TOKEN"));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // procesarReembolso
    // ═════════════════════════════════════════════════════════════════════════

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
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepción si estado REEMBOLSADO no existe en BD")
        void estadoReembolsadoNoExiste() {
            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion(1L, 50000.0)));
            when(estadoPagoRepository.findByNombre("REEMBOLSADO")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.procesarReembolso(1L, "Motivo"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("REEMBOLSADO");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // generarFactura
    // ═════════════════════════════════════════════════════════════════════════

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
            assertThat(factura.getXmlDocumento()).contains("1").contains("80000.0");
            verify(facturaRepository).save(any(FacturaElectronica.class));
        }

        @Test
        @DisplayName("lanza excepción si transacción no existe")
        void transaccionNoExiste() {
            when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.generarFactura(99L, 12345678L, "Giro"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");

            verify(facturaRepository, never()).save(any());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // enviarBoletaEmail
    // ═════════════════════════════════════════════════════════════════════════

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
        @DisplayName("retorna true incluso con correo vacío (flujo actual)")
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
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // obtenerTransaccion
    // ═════════════════════════════════════════════════════════════════════════

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
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }
    }
}