package com.challenge.java.tomi.exception;

import java.io.Serial;

/**
 * Exception containing relevant information for existing transactions.
 */
public class TransactionExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3098877158397021020L;

    /**
     * Creates a new instance, with custom message and exception.
     *
     * @param message custom exception message.
     */
    public TransactionExistsException(String message) {
        super(message);
    }
}
