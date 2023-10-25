package com.challenge.java.tomi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handler for already exists exception.
     * @param e the exception thrown during request processing.
     * @return ResponseEntity with 409 status code and description indicating an already exists error.
     */
    @ExceptionHandler({AlreadyExistsException.class})
    protected ResponseEntity<ApiError> handleAlreadyExistsException(AlreadyExistsException e) {
        LOGGER.warn("Already exists exception", e);

        ApiError apiError =
                new ApiError("already_exists", e.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(apiError.getStatusCode()).body(apiError);
    }

    /**
     * Handler for illegal argument exception.
     * @param e the exception thrown during request processing.
     * @return ResponseEntity with 400 status code and description indicating an illegal argument error.
     */
    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {
        LOGGER.error("Internal validation exception", e);
        ApiError apiError =
                new ApiError("illegal_argument", e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(apiError.getStatusCode()).body(apiError);
    }

    /**
     * Handler for not found exception.
     * @param e the exception thrown during request processing.
     * @return ResponseEntity with 404 status code and description indicating a not found error.
     */
    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        LOGGER.warn("Not found exception", e);
        ApiError apiError =
                new ApiError("not_found", e.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(apiError.getStatusCode()).body(apiError);
    }
}
