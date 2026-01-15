package dev.benwilliams.am_i_detained.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails handleNotFound(NoSuchElementException ex) {
        return new ErrorDetails(
            LocalDateTime.now(),
            404,
            "Not Found",
            "Resource not found"
        );
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDetails handleUnauthorized(SecurityException ex) {
        return new ErrorDetails(
            LocalDateTime.now(),
            403,
            "Forbidden",
            ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails handleGeneral(Exception ex) {
        return new ErrorDetails(
            LocalDateTime.now(),
            500,
            "Internal Server Error",
            ex.getMessage()
        );
    }
}
