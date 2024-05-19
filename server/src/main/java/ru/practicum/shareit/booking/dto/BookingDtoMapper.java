package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingDtoMapper {
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
}