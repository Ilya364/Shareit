package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.ValidationException;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
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

/*    @AssertTrue(message = "The end date of the booking must be later than the start date.")
    public boolean isEndAfterStart() {
        if (start != null && end != null) {
            return end.isAfter(start);
        } else {
            throw new ValidationException("End or start null");
        }
    }*/
}