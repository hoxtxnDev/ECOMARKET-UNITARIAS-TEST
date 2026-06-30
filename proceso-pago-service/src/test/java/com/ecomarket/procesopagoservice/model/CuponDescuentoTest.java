package com.ecomarket.procesopagoservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CuponDescuentoTest {

    @Test
    void esValido_activoYNoExpirado_retornaTrue() {
        CuponDescuento cupon = new CuponDescuento(1L, "CUPON10", 10.0, 5000.0,
                LocalDateTime.now().plusDays(1), true);
        assertTrue(cupon.esValido());
    }

    @Test
    void esValido_inactivo_retornaFalse() {
        CuponDescuento cupon = new CuponDescuento(1L, "CUPON10", 10.0, 5000.0,
                LocalDateTime.now().plusDays(1), false);
        assertFalse(cupon.esValido());
    }

    @Test
    void esValido_activoNulo_retornaFalse() {
        CuponDescuento cupon = new CuponDescuento(1L, "CUPON10", 10.0, 5000.0,
                LocalDateTime.now().plusDays(1), null);
        assertFalse(cupon.esValido());
    }

    @Test
    void esValido_expirado_retornaFalse() {
        CuponDescuento cupon = new CuponDescuento(1L, "CUPON10", 10.0, 5000.0,
                LocalDateTime.now().minusDays(1), true);
        assertFalse(cupon.esValido());
    }

    @Test
    void esValido_fechaExpiracionNula_retornaFalse() {
        CuponDescuento cupon = new CuponDescuento(1L, "CUPON10", 10.0, 5000.0,
                null, true);
        assertFalse(cupon.esValido());
    }
}
