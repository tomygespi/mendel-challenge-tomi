package com.challenge.java.tomi.exception;

import java.io.Serial;

/**
 * Exception containing relevant information for existing resources.
 */
public class AlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5868215503978769646L;

    /**
     * Creates a new instance, with custom message.
     *
     * @param message custom exception message.
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}
