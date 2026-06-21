package com.ecomarket.carritocompraservice.exception;

public class NoExisteEnBdException extends RuntimeException {
    public NoExisteEnBdException(String message) {
        super(message);
    }
}
