package com.ecomarket.gestiontiendaservice.exception;

public class YaExisteEnBdException extends RuntimeException {

    public YaExisteEnBdException(String mensaje) {
        super(mensaje);
    }
}
