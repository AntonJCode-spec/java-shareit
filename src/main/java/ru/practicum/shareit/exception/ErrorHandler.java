package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundHandler(NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.toString(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationExceptionHandler(DataValidationException e) {
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(
                new ErrorResponse("VALIDATION_ERROR", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> duplicateExceptionHandler(DuplicateDataException e) {
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> authorizationExceptionHandler(NonAuthorizedUserException e) {
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", e.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_SERVER", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
