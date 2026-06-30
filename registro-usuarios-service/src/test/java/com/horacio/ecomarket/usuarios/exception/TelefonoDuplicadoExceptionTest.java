package com.horacio.ecomarket.usuarios.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TelefonoDuplicadoException")
class TelefonoDuplicadoExceptionTest {

    @Test
    @DisplayName("construye excepción con mensaje")
    void construyeConMensaje() {
        TelefonoDuplicadoException ex = new TelefonoDuplicadoException("El teléfono ya está registrado.");
        assertThat(ex.getMessage()).isEqualTo("El teléfono ya está registrado.");
    }
}
