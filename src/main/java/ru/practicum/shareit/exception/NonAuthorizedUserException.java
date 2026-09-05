package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NonAuthorizedUserException extends RuntimeException {
    public NonAuthorizedUserException(String message) {
        super(message);
    }
}
