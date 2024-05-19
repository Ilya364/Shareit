package ru.practicum.shareit.exception;

public class NonUniqueEmailException extends RuntimeException {
    public NonUniqueEmailException(String message) {
        super(message);
    }
}