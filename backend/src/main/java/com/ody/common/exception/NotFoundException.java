package com.ody.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {
    public static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }
}
