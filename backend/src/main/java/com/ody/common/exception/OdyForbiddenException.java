package com.ody.common.exception;

import org.springframework.http.HttpStatus;

public class OdyForbiddenException extends OdyException {

    public OdyForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
