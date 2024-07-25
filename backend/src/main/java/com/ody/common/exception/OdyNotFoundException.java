package com.ody.common.exception;

import org.springframework.http.HttpStatus;

public class OdyNotFoundException extends OdyException {

    public OdyNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
