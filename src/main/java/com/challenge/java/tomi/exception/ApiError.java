package com.challenge.java.tomi.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {

    private String error;

    private String message;

    private Integer statusCode;

    /**
     * Creates a new instance, with empty fields.
     */
    public ApiError() {
    }

    /**
     * Creates a new instance, with all fields.
     * @param error short error description.
     * @param message full error message.
     * @param statusCode HTTP status code.
     */
    public ApiError(String error, String message, Integer statusCode) {
        this.error = error;
        this.message = message;
        this.statusCode = statusCode;
    }
}
