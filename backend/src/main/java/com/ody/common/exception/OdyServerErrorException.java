package com.ody.common.exception;

import org.springframework.http.HttpStatus;

public class OdyServerErrorException extends OdyException {

    public OdyServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
