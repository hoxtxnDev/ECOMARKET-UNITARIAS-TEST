package com.ecomarket.gestiontiendaservice.exception;

public class NoExisteEnBdException extends RuntimeException {

    public NoExisteEnBdException(String mensaje) {
        super(mensaje);
    }
}
