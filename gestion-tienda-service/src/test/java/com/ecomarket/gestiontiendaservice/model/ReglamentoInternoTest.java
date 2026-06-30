package com.ecomarket.gestiontiendaservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ReglamentoInternoTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime vigencia = LocalDateTime.now().plusDays(30);
        ReglamentoInterno r = new ReglamentoInterno(1L, 10L, 1, "Título", "Contenido legal", vigencia);
        assertEquals(1L, r.getId());
        assertEquals(10L, r.getSucursalId());
        assertEquals(1, r.getVersion());
        assertEquals("Título", r.getTituloSeccion());
        assertEquals("Contenido legal", r.getContenidoLegal());
        assertEquals(vigencia, r.getFechaVigencia());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        ReglamentoInterno r = new ReglamentoInterno();
        r.setId(2L);
        r.setVersion(2);
        r.setTituloSeccion("Sección 2");
        assertEquals(2, r.getVersion());
        assertEquals("Sección 2", r.getTituloSeccion());
    }
}
