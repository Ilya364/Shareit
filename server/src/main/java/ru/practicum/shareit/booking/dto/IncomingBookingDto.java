package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncomingBookingDto {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
}