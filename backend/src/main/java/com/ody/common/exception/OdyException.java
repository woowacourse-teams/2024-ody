package com.ody.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class OdyException extends RuntimeException {

    private final HttpStatus httpStatus;

    public OdyException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
