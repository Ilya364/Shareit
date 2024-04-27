package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingDtoMapper {
    public boolean isEndAfterStart(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return end.isAfter(start);
        } else {
            throw new ValidationException("End or start null");
        }
    }

    public OutgoingBookingDto toOutgoingDto(Booking booking) {
        return OutgoingBookingDto.builder()
            .id(booking.getId())
            .item(booking.getItem())
            .booker(booking.getBooker())
            .start(booking.getStart())
            .status(booking.getStatus())
            .end(booking.getEnd())
            .build();
    }

    public OutForItemBooking toOutForItemDto(Booking booking) {
        return OutForItemBooking.builder()
            .id(booking.getId())
            .itemId(booking.getItem().getId())
            .bookerId(booking.getBooker().getId())
            .start(booking.getStart())
            .status(booking.getStatus())
            .end(booking.getEnd())
            .build();
    }

    public Booking toBooking(IncomingBookingDto dto) {
        if (!isEndAfterStart(dto.getStart(), dto.getEnd())) {
            throw new ValidationException("Start after end.");
        }
        return Booking.builder()
            .status(Status.WAITING)
            .start(dto.getStart())
            .end(dto.getEnd())
            .build();
    }

    public List<OutgoingBookingDto> toOutgoingDtoList(List<Booking> bookings) {
        return bookings.stream()
            .map(BookingDtoMapper::toOutgoingDto)
            .collect(Collectors.toList());
    }

    public List<OutForItemBooking> toOutForItemDtoList(List<Booking> bookings) {
        return bookings.stream()
            .map(BookingDtoMapper::toOutForItemDto)
            .collect(Collectors.toList());
    }

    public List<Booking> toBookingList(List<IncomingBookingDto> dtos) {
        return dtos.stream()
            .map(BookingDtoMapper::toBooking)
            .collect(Collectors.toList());
    }

    public void partialUpdateBooking(IncomingBookingDto dto, Booking booking) {
        Field[] dtoFields = dto.getClass().getDeclaredFields();
        Class userClass = booking.getClass();
        for (Field field: dtoFields) {
            try {
                field.setAccessible(true);
                userClass.getDeclaredField(field.getName()).set(booking, field.get(dto));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}