package ru.practicim.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@Validated
public class BookingDto {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    @Positive
    private Long itemId;
    @Positive
    private Long bookerId;

    @AssertTrue(message = "Time validation error")
    private boolean isTimeValid() {
        if (start == null || end == null) {
            return false;
        }
        return !(start.equals(end) || end.isBefore(start));
    }
}
