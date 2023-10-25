package com.challenge.java.tomi.exception;

import java.io.Serial;

/**
 * Exception containing relevant information for not found resources.
 */
public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5757097831084622021L;

    /**
     * Creates a new instance, with custom message.
     *
     * @param message custom exception message.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
