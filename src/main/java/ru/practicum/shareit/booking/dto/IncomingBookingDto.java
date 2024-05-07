package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomingBookingDto {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    @NotNull
    @Positive
    private Long itemId;
    @Positive
    private Long bookerId;
}