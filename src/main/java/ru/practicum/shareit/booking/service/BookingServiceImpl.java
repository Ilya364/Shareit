package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongUserIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private Booking getBookingIfUserHasAccess(Long bookingId, Long userId) {
        Booking booking;
        try {
            booking = isBooker(userId, bookingId);
        } catch (NotFoundException e) {
            booking = isItemOwner(userId, bookingId);
        }
        return booking;
    }

    private Booking isBooker(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        boolean isUserBooker = booking.getBooker().getId().equals(userId);
        if (isUserBooker) {
            return booking;
        } else {
            throw new NotFoundException("User " + userId + " is not booker of booking " + bookingId + ".");
        }
    }

    private Booking isItemOwner(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        boolean isUserItemOwner = booking.getItem().getOwner().getId().equals(userId);
        if (isUserItemOwner) {
            return booking;
        } else {
            throw new NotFoundException("User " + userId + " is not owner of item " + booking.getItem().getId() + ".");
        }
    }

    private Booking getBookingById(Long bookingId) {
        try {
            return bookingRepository.findById(bookingId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Booking " + bookingId + " is not found.");
        }
    }

    private boolean isBookingApproved(Booking booking) {
        return booking.getStatus().equals(Status.APPROVED);
    }

    private User getUserById(Long userId) {
        try {
            return userRepository.findById(userId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new WrongUserIdException("User " + userId + " is not exist.");
        }
    }

    private Item getItemById(Long itemId) {
        try {
            return itemRepository.findById(itemId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new WrongUserIdException("Item " + itemId + " is not exist.");
        }
    }

    private Predicate<Booking> getFilterByState(State state) {
        switch (state) {
            case PAST:
                return booking -> booking.getEnd().isBefore(LocalDateTime.now());
            case CURRENT:
                return booking ->
                    booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now());

            case FUTURE:
                return booking -> booking.getStart().isAfter(LocalDateTime.now());
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public Booking createBooking(Booking booking, Long bookerId) {
        User user = getUserById(bookerId);
        Item item = getItemById(booking.getItem().getId());
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Booker can't be an item owner.");
        } else if (!item.getAvailable()) {
            throw new ValidationException("Item " + item.getId() + " is unavailable.");
        }
        booking.setItem(item);
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        return getBookingIfUserHasAccess(bookingId, userId);
    }

    @Override
    public void deleteBookingById(Long bookingId, Long userId) {
        isBooker(userId, bookingId);
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public List<Booking> getUserBookings(Long bookerId, State state) {
        User booker = getUserById(bookerId);
        List<Booking> bookings;
        if (state.equals(State.ALL)) {
            bookings = new ArrayList<>(bookingRepository.findAllByBookerOrderByStartDesc(booker));
        } else {
            try {
                Status status = Status.valueOf(state.name());
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, status);
            } catch (IllegalArgumentException e) {
                bookings = bookingRepository.findAllByBookerOrderByStartDesc(booker).stream()
                    .filter(getFilterByState(state))
                    .collect(Collectors.toList());
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> getItemOwnerBookings(Long itemOwnerId, State state) {
        User itemOwner = getUserById(itemOwnerId);
        List<Booking> bookings;
        if (state.equals(State.ALL)) {
            bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerOrderByStartDesc(itemOwner));
        } else {
            try {
                Status status = Status.valueOf(state.name());
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(itemOwner, status);
            } catch (IllegalArgumentException e) {
                bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(itemOwner).stream()
                    .filter(getFilterByState(state))
                    .collect(Collectors.toList());
            }
        }
        return bookings;
    }

    @Override
    public Booking approveBooking(Long bookingId, Long itemOwnerId) {
        Booking booking = isItemOwner(itemOwnerId, bookingId);
        if (isBookingApproved(booking)) {
            throw new ValidationException("Booking " + bookingId + " is already approved.");
        }
        booking.setStatus(Status.APPROVED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking rejectBooking(Long bookingId, Long itemOwnerId) {
        Booking booking = isItemOwner(itemOwnerId, bookingId);
        if (isBookingApproved(booking)) {
            throw new ValidationException("Booking " + bookingId + " is already approved.");
        }
        booking.setStatus(Status.REJECTED);
        return bookingRepository.save(booking);
    }
}