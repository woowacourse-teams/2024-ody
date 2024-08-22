package com.ody.common.exception;

import org.springframework.http.HttpStatus;

public class OdyBadRequestException extends OdyException {

    public OdyBadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
