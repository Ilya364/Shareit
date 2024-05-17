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
    public ErrorDto handleValidationException(final IllegalArgumentException  exception) {
        log.info("Validation exception {}", exception.getMessage());
        String message = exception.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(final Exception exception) {
        StringWriter error = new StringWriter();
        exception.printStackTrace(new PrintWriter(error));
        log.error("Exception: ", exception);
        return new ErrorDto(exception.getMessage(), error.toString());
    }
}
