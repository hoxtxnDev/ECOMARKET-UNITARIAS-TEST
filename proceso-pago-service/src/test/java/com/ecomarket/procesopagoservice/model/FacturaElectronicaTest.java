package com.ecomarket.procesopagoservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class FacturaElectronicaTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        FacturaElectronica f = new FacturaElectronica(1L, 1000L, 10L, 5L, "12345678-9", "Empresa S.A.", "<xml/>", now);
        assertEquals(1L, f.getId());
        assertEquals(1000L, f.getFolioFiscal());
        assertEquals(10L, f.getTransaccionId());
        assertEquals(5L, f.getClienteId());
        assertEquals("12345678-9", f.getRutReceptor());
        assertEquals("Empresa S.A.", f.getRazonSocial());
        assertEquals("<xml/>", f.getXmlDocumento());
        assertEquals(now, f.getFechaEmision());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        FacturaElectronica f = new FacturaElectronica();
        f.setId(1L);
        f.setRutReceptor("98765432-1");
        f.setRazonSocial("Otra S.A.");
        assertEquals("98765432-1", f.getRutReceptor());
        assertEquals("Otra S.A.", f.getRazonSocial());
    }
}
