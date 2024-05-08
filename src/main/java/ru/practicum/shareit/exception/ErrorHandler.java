package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.user.UserController;
import java.util.Arrays;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {
    ItemController.class, UserController.class, BookingController.class, ItemRequestController.class
})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.error("Requesting a non-existent resource: " + e.getMessage());
        return Map.of("Resource is not found:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNoAccessException(final BookingNoAccessException e) {
        log.error("A request from a non-owner user: " + e.getMessage());
        return Map.of("A request from a non-owner user:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("The request was not validated: " + e.getMessage());
        return Map.of("The request was not validated:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.error("The request was not validated: " + e.getMessage());
        return Map.of("The request was not validated:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupportedException(final UnsupportedStateException e) {
        log.error("Unknown state: " + e.getMessage());
        return Map.of("error", "Unknown state: " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNonUniqueEmailException(final NonUniqueEmailException e) {
        log.error("Email is not-unique: " + e.getMessage());
        return Map.of("Email is not-unique:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalError(final Throwable e) {
        log.error("Internal server error: " + e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
        return Map.of("Internal server error:", e.getMessage());
    }
}