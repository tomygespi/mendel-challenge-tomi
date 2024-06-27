package com.challenge.java.tomi.exception;

import java.io.Serial;

/**
 * Exception containing relevant information for not found transactions.
 */
public class TransactionNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6382703917847723333L;

    /**
     * Creates a new instance, with custom message and exception.
     *
     * @param message custom exception message.
     */
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
