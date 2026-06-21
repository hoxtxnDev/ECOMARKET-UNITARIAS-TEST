package com.ecomarket.procesopagoservice.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class EstadoTransaccionInvalidoException extends RuntimeException {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public EstadoTransaccionInvalidoException(String message) {
        super(message);
    }
}
