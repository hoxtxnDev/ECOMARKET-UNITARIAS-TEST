package com.ecomarket.procesopagoservice.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class RecursoNoEncontradoException extends RuntimeException {
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
