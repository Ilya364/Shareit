package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Long owner);

    Booking getBookingById(Long id, Long user);

    void deleteBookingById(Long bookingId, Long userId);

    List<Booking> getUserBookings(Long userId, State state, Integer from, Integer size);

    List<Booking> getItemOwnerBookings(Long ownerId, State state, Integer from, Integer size);

    List<Booking> getUserBookings(Long userId, State state);

    List<Booking> getItemOwnerBookings(Long ownerId, State state);

    Booking approveBooking(Long bookingId, Long itemOwnerId);

    Booking rejectBooking(Long bookingId, Long itemOwnerId);
}