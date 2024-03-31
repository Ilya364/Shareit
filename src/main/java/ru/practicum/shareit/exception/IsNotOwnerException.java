package ru.practicum.shareit.exception;

public class IsNotOwnerException extends RuntimeException {
    public IsNotOwnerException(String message) {
        super(message);
    }
}