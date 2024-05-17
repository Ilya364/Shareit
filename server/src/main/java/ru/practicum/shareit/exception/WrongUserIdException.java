package ru.practicum.shareit.exception;

public class WrongUserIdException extends RuntimeException {
    public WrongUserIdException(String message) {
        super(message);
    }
}