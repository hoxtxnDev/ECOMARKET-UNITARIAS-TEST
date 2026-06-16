package com.ecomarket.analiticaservice.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NoExisteEnBdExceptionTest {

    @Test
    void constructorSetsMessage() {
        NoExisteEnBdException ex = new NoExisteEnBdException("Test error");
        assertEquals("Test error", ex.getMessage());
    }
}
