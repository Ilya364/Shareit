package ru.practicum.shareit.exception;

public class BookingNoAccessException extends RuntimeException {
    public BookingNoAccessException(String message) {
        super(message);
    }
}