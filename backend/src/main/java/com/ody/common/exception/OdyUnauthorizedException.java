package com.ody.common.exception;

import org.springframework.http.HttpStatus;

public class OdyUnauthorizedException extends OdyException {

    public OdyUnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
