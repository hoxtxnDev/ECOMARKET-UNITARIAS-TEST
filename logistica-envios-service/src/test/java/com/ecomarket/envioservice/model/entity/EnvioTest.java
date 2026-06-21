package com.ecomarket.envioservice.model.entity;

import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.model.reference.MetodoEnvio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Envio")
class EnvioTest {

    private Envio envio() {
        Envio e = new Envio();
        e.setId(1L);
        e.setPedidoId(100L);
        e.setClienteId(5L);
        e.setEstadoActual(new EstadoEnvio(1L, "PENDIENTE"));
        e.setCostoEnvio(5000.0);
        e.setDireccionId(1L);
        return e;
    }

    @Nested @DisplayName("calcularFechaEstimada")
    class CalcularFechaEstimada {
        @Test @DisplayName("retorna null si fechaCreacion es null")
        void fechaCreacionNull() {
            assertThat(envio().calcularFechaEstimada()).isNull();
        }

        @Test @DisplayName("retorna fechaCreacion + 2 dias si metodo es PuntoRetiro")
        void puntoRetiro() {
            Envio e = envio();
            e.setFechaCreacion(LocalDateTime.of(2026, 6, 16, 10, 0));
            e.setMetodoEnvio(new MetodoEnvio(1L, "PuntoRetiro", 0.0));
            assertThat(e.calcularFechaEstimada()).isEqualTo(
                    LocalDateTime.of(2026, 6, 18, 10, 0));
        }

        @Test @DisplayName("retorna fechaCreacion + 5 dias por defecto")
        void defecto() {
            Envio e = envio();
            e.setFechaCreacion(LocalDateTime.of(2026, 6, 16, 10, 0));
            e.setMetodoEnvio(new MetodoEnvio(1L, "Domicilio", 5000.0));
            assertThat(e.calcularFechaEstimada()).isEqualTo(
                    LocalDateTime.of(2026, 6, 21, 10, 0));
        }

        @Test @DisplayName("retorna fechaCreacion + 5 dias si metodoEnvio es null")
        void metodoEnvioNull() {
            Envio e = envio();
            e.setFechaCreacion(LocalDateTime.of(2026, 6, 16, 10, 0));
            e.setMetodoEnvio(null);
            assertThat(e.calcularFechaEstimada()).isEqualTo(
                    LocalDateTime.of(2026, 6, 21, 10, 0));
        }
    }
}
