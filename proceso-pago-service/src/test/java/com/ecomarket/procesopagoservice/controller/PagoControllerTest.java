package com.ecomarket.procesopagoservice.controller;

import com.ecomarket.procesopagoservice.model.*;
import com.ecomarket.procesopagoservice.service.PagoService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para PagoController.
 *
 * Ejecutar:
 *   mvn test -pl proceso-pago-service -Dtest=PagoControllerTest
 */
@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("PagoController")
class PagoControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean PagoService pagoService;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

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

    private TransaccionPago transaccion(Long id) {
        TransaccionPago t = new TransaccionPago();
        t.setId(id);
        t.setPedidoId(10L);
        t.setClienteId(5L);
        t.setMontoSubtotal(50000.0);
        t.setMontoDescuento(0.0);
        t.setMontoTotal(50000.0);
        t.setMetodoPago(metodo());
        t.setEstado(estado("PENDIENTE"));
        return t;
    }

    private FacturaElectronica factura(Long id) {
        FacturaElectronica f = new FacturaElectronica();
        f.setId(id);
        f.setTransaccionId(1L);
        f.setClienteId(5L);
        f.setRutReceptor("12345678");
        f.setRazonSocial("Comercio SA");
        f.setFolioFiscal(System.currentTimeMillis());
        f.setXmlDocumento("<factura><transaccion>1</transaccion></factura>");
        f.setFechaEmision(LocalDateTime.now());
        return f;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // POST /api/pagos/iniciar
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /iniciar")
    class IniciarPago {

        @Test
        @DisplayName("200 OK al iniciar pago con parámetros válidos")
        void exitoso() throws Exception {
            when(pagoService.iniciarPago(eq(10L), eq(5L), eq(50000.0), any(MetodoPagoTransaccion.class)))
                    .thenReturn(transaccion(1L));

            mvc.perform(post("/api/pagos/iniciar")
                            .param("pedidoId", "10")
                            .param("clienteId", "5")
                            .param("monto", "50000.0")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(metodo())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.montoTotal").value(50000.0))
                    .andExpect(jsonPath("$.estado.nombre").value("PENDIENTE"));
        }

        @Test
        @DisplayName("400 si el service lanza RuntimeException (estado PENDIENTE no existe)")
        void estadoNoExiste() throws Exception {
            when(pagoService.iniciarPago(anyLong(), anyLong(), any(Double.class), any()))
                    .thenThrow(new RuntimeException("Estado PENDIENTE no encontrado"));

            mvc.perform(post("/api/pagos/iniciar")
                            .param("pedidoId", "10")
                            .param("clienteId", "5")
                            .param("monto", "50000.0")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(metodo())))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // GET /api/pagos/{transaccionId}
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /{transaccionId}")
    class ObtenerTransaccion {

        @Test
        @DisplayName("200 OK al obtener transacción existente")
        void exitoso() throws Exception {
            when(pagoService.obtenerTransaccion(1L)).thenReturn(transaccion(1L));

            mvc.perform(get("/api/pagos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.clienteId").value(5));
        }

        @Test
        @DisplayName("400 si transacción no existe")
        void noExiste() throws Exception {
            when(pagoService.obtenerTransaccion(99L))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(get("/api/pagos/99"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // POST /api/pagos/{transaccionId}/cupon/{cuponId}
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /{transaccionId}/cupon/{cuponId}")
    class AnadirCupon {

        @Test
        @DisplayName("200 OK con descuento aplicado")
        void exitoso() throws Exception {
            TransaccionPago conDescuento = transaccion(1L);
            conDescuento.setMontoDescuento(5000.0);
            conDescuento.setMontoTotal(45000.0);
            conDescuento.setCuponUtilizadoId(1L);

            when(pagoService.anadirCuponDescuento(1L, 1L)).thenReturn(conDescuento);

            mvc.perform(post("/api/pagos/1/cupon/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.montoDescuento").value(5000.0))
                    .andExpect(jsonPath("$.montoTotal").value(45000.0))
                    .andExpect(jsonPath("$.cuponUtilizadoId").value(1));
        }

        @Test
        @DisplayName("400 si cupón expirado o inválido")
        void cuponInvalido() throws Exception {
            when(pagoService.anadirCuponDescuento(1L, 2L))
                    .thenThrow(new RuntimeException("El cupón no es válido o está expirado"));

            mvc.perform(post("/api/pagos/1/cupon/2"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 si transacción no existe")
        void transaccionNoExiste() throws Exception {
            when(pagoService.anadirCuponDescuento(99L, 1L))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(post("/api/pagos/99/cupon/1"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // POST /api/pagos/{transaccionId}/transbank
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /{transaccionId}/transbank")
    class ProcesarTransbank {

        @Test
        @DisplayName("200 OK con estado APROBADO y token guardado")
        void exitoso() throws Exception {
            TransaccionPago aprobado = transaccion(1L);
            aprobado.setEstado(estado("APROBADO"));
            aprobado.setTokenTransbank("TOKEN-XYZ");
            aprobado.setCodigoAutorizacion("AUTH-001");
            aprobado.setFechaAutorizacion(LocalDateTime.now());

            when(pagoService.procesarConTransbank(1L, "TOKEN-XYZ")).thenReturn(aprobado);

            mvc.perform(post("/api/pagos/1/transbank")
                            .param("token", "TOKEN-XYZ"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado.nombre").value("APROBADO"))
                    .andExpect(jsonPath("$.tokenTransbank").value("TOKEN-XYZ"));
        }

        @Test
        @DisplayName("400 si transacción no existe")
        void transaccionNoExiste() throws Exception {
            when(pagoService.procesarConTransbank(99L, "TOKEN"))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(post("/api/pagos/99/transbank")
                            .param("token", "TOKEN"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // POST /api/pagos/{transaccionId}/reembolso
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /{transaccionId}/reembolso")
    class ProcesarReembolso {

        @Test
        @DisplayName("200 OK retorna true al procesar reembolso")
        void exitoso() throws Exception {
            when(pagoService.procesarReembolso(1L, "Producto dañado")).thenReturn(true);

            mvc.perform(post("/api/pagos/1/reembolso")
                            .param("motivo", "Producto dañado"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("400 si transacción no existe")
        void transaccionNoExiste() throws Exception {
            when(pagoService.procesarReembolso(99L, "Motivo"))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(post("/api/pagos/99/reembolso")
                            .param("motivo", "Motivo"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // POST /api/pagos/{transaccionId}/factura
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /{transaccionId}/factura")
    class GenerarFactura {

        @Test
        @DisplayName("200 OK retorna factura generada")
        void exitoso() throws Exception {
            when(pagoService.generarFactura(1L, 12345678L, "Comercio SA")).thenReturn(factura(1L));

            mvc.perform(post("/api/pagos/1/factura")
                            .param("rut", "12345678")
                            .param("giro", "Comercio SA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.rutReceptor").value("12345678"))
                    .andExpect(jsonPath("$.razonSocial").value("Comercio SA"));
        }

        @Test
        @DisplayName("400 si transacción no existe")
        void transaccionNoExiste() throws Exception {
            when(pagoService.generarFactura(99L, 12345678L, "Giro"))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(post("/api/pagos/99/factura")
                            .param("rut", "12345678")
                            .param("giro", "Giro"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // POST /api/pagos/{transaccionId}/email
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /{transaccionId}/email")
    class EnviarBoletaEmail {

        @Test
        @DisplayName("200 OK al enviar boleta con correo")
        void exitoso() throws Exception {
            when(pagoService.enviarBoletaEmail(1L, "cliente@test.cl")).thenReturn(true);

            mvc.perform(post("/api/pagos/1/email")
                            .param("correoDestino", "cliente@test.cl"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("200 OK sin correoDestino (usa default vacío)")
        void sinCorreo() throws Exception {
            when(pagoService.enviarBoletaEmail(1L, "")).thenReturn(true);

            mvc.perform(post("/api/pagos/1/email"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("400 si transacción no existe")
        void transaccionNoExiste() throws Exception {
            when(pagoService.enviarBoletaEmail(99L, ""))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(post("/api/pagos/99/email"))
                    .andExpect(status().isBadRequest());
        }
    }
}