package ru.practicim.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicim.shareit.exception.dto.ErrorDto;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationException(final IllegalArgumentException  e) {
        log.info("Validation exception {}", e.getMessage());
        String message = e.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(final RuntimeException e) {
        StringWriter error = new StringWriter();
        e.printStackTrace(new PrintWriter(error));
        log.error("Exception: ", e);
        return new ErrorDto(e.getMessage(), error.toString());
    }
}
