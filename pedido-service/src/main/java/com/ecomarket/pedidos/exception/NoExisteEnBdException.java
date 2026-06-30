package com.ecomarket.pedidos.exception;

public class NoExisteEnBdException extends RuntimeException {
    public NoExisteEnBdException(String message) {
        super(message);
    }
}
