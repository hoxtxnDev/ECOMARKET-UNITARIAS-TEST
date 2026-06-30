package com.ecomarket.procesopagoservice.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class CuponInvalidoException extends RuntimeException {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public CuponInvalidoException(String message) {
        super(message);
    }
}
