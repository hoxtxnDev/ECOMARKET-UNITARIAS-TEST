package com.ecomarket.pedidos.exception;

public class YaExisteEnBdException extends RuntimeException {
    public YaExisteEnBdException(String message) {
        super(message);
    }
}
