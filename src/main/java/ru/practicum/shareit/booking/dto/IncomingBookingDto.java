package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomingBookingDto {
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