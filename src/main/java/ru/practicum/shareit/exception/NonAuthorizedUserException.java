package ru.practicum.shareit.exception;

public class NonAuthorizedUserException extends RuntimeException {
    public NonAuthorizedUserException(String message) {
        super(message);
    }
}
