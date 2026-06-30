package com.ecomarket.procesopagoservice.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ProcesamientoPagoException extends RuntimeException {
    private final HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;

    public ProcesamientoPagoException(String message) {
        super(message);
    }
}
