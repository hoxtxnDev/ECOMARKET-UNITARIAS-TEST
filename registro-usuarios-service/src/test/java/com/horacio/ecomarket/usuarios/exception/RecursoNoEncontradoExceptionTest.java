package com.horacio.ecomarket.usuarios.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RecursoNoEncontradoException")
class RecursoNoEncontradoExceptionTest {

    @Test
    @DisplayName("Debería crear excepción con el mensaje dado")
    void testMensaje() {
        RecursoNoEncontradoException ex = new RecursoNoEncontradoException("Dirección no encontrada.");
        assertThat(ex.getMessage()).isEqualTo("Dirección no encontrada.");
    }

    @Test
    @DisplayName("Debería extender RuntimeException")
    void testEsRuntimeException() {
        RecursoNoEncontradoException ex = new RecursoNoEncontradoException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
