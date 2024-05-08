package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.*;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public OutgoingBookingDto createBooking(
        @Valid @RequestBody IncomingBookingDto incomingBookingDto,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to create Booking.");
        Item item = itemService.getItem(incomingBookingDto.getItemId());
        Booking booking = toBooking(incomingBookingDto);
        booking.setItem(item);
        return toOutgoingDto(bookingService.createBooking(booking, user));
    }

    @GetMapping("/{bookingId}")
    public OutgoingBookingDto getBookingById(
        @PathVariable Long bookingId,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to receive Booking {}.", bookingId);
        return toOutgoingDto(bookingService.getBookingById(bookingId, user));
    }

    @PatchMapping("/{bookingId}")
    public OutgoingBookingDto approveOrRejectBooking(
        @RequestParam("approved") Boolean approved,
        @PathVariable Long bookingId,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        if (approved) {
            log.info("Request to approve Booking.");
            return toOutgoingDto(bookingService.approveBooking(bookingId, user));
        } else {
            log.info("Request to reject Booking.");
            return toOutgoingDto(bookingService.rejectBooking(bookingId, user));
        }
    }

    @GetMapping
    public List<OutgoingBookingDto> getUserBookings(
        @RequestParam(value = "state", defaultValue = "ALL") String state,
        @RequestParam(value = "from", required = false) Integer from,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestHeader(USER_ID_HEADER) Long user
        ) {
        log.info("Request to receive user {}' Booking.", user);
        try {
            if (from == null || size == null) {
                return toOutgoingDtoList(bookingService.getUserBookings(user, State.valueOf(state)));
            }
            return toOutgoingDtoList(bookingService.getUserBookings(user, State.valueOf(state), from, size));
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException(state);
        }
    }

    @GetMapping("/owner")
    public List<OutgoingBookingDto> getItemOwnerBookings(
        @RequestParam(value = "state", defaultValue = "ALL") String state,
        @RequestParam(value = "from", required = false) Integer from,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to receive item owner {}' Booking.", user);
        try {
            if (from == null || size == null) {
                return toOutgoingDtoList(bookingService.getItemOwnerBookings(user, State.valueOf(state)));
            }
            return toOutgoingDtoList(bookingService.getItemOwnerBookings(user, State.valueOf(state), from, size));
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException(state);
        }
    }
}