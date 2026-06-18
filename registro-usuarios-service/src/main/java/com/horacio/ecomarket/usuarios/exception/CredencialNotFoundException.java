package com.horacio.ecomarket.usuarios.exception;

public class CredencialNotFoundException extends RuntimeException {

    public CredencialNotFoundException(String mensaje) {
        super(mensaje);
    }
}
