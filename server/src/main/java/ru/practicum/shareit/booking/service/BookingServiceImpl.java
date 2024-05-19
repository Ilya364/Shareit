package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort byStartDescSorting = Sort.by(Sort.Direction.DESC, "start");

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
            throw new NotFoundException(String.format("User %d is not booker of booking %d.", userId, bookingId));
        }
    }

    private Booking isItemOwner(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        boolean isUserItemOwner = booking.getItem().getOwner().getId().equals(userId);
        if (isUserItemOwner) {
            return booking;
        } else {
            throw new NotFoundException(
                String.format("User %d is not owner of item %d.", userId, booking.getItem().getId())
            );
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
            () -> new NotFoundException(String.format("Booking %d is not found.", bookingId))
        );
    }

    private boolean isBookingApproved(Booking booking) {
        return booking.getStatus().equals(Status.APPROVED);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new WrongUserIdException(String.format("User %d is not exist.", userId))
        );
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
            () -> new WrongUserIdException(String.format("Item %d is not exist.", itemId))
        );
    }

    private Predicate<Booking> getFilterByState(State state) {
        switch (state) {
            case PAST:
                return booking ->
                    booking.getEnd().isBefore(LocalDateTime.now());
            case CURRENT:
                return booking ->
                    booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now());
            case FUTURE:
                return booking ->
                    booking.getStart().isAfter(LocalDateTime.now());
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
            throw new ValidationException(String.format("Item %d is unavailable.", item.getId()));
        }
        booking.setItem(item);
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, Long userId) {
        return getBookingIfUserHasAccess(bookingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long bookerId, State state, Integer from, Integer size) {
        User booker = getUserById(bookerId);
        List<Booking> bookings = new ArrayList<>();

        Pagination pagination = new Pagination(from, size);
        Pageable page = pagination.getPageable();
        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByBooker(booker, page));
                break;
            case WAITING:
            case REJECTED:
            case CANCELLED:
                Status status = Status.valueOf(state.name());
                bookings = bookingRepository.findAllByBookerAndStatus(booker, status, page);
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findAllByBooker(booker, page).stream()
                    .filter(getFilterByState(state))
                    .collect(Collectors.toList());
        }
        return bookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getItemOwnerBookings(Long itemOwnerId, State state, Integer from, Integer size) {
        User itemOwner = getUserById(itemOwnerId);
        List<Booking> bookings = new ArrayList<>();

        Pagination pagination = new Pagination(from, size);
        Pageable page = pagination.getPageable();
        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwner(itemOwner, page));
                break;
            case WAITING:
            case REJECTED:
            case CANCELLED:
                Status status = Status.valueOf(state.name());
                bookings = bookingRepository.findAllByItemOwnerAndStatus(itemOwner, status, page);
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwner(itemOwner, page).stream()
                    .filter(getFilterByState(state))
                    .collect(Collectors.toList());
        }
        return bookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long bookerId, State state) {
        User booker = getUserById(bookerId);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByBooker(booker, byStartDescSorting));
                break;
            case WAITING:
            case REJECTED:
            case CANCELLED:
                Status status = Status.valueOf(state.name());
                bookings = bookingRepository.findAllByBookerAndStatus(booker, status, byStartDescSorting);
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findAllByBooker(booker, byStartDescSorting).stream()
                    .filter(getFilterByState(state))
                    .collect(Collectors.toList());
            }
        return bookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getItemOwnerBookings(Long ownerId, State state) {
        User itemOwner = getUserById(ownerId);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwner(itemOwner, byStartDescSorting));
                break;
            case WAITING:
            case REJECTED:
            case CANCELLED:
                Status status = Status.valueOf(state.name());
                bookings = bookingRepository.findAllByItemOwnerAndStatus(itemOwner, status, byStartDescSorting);
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwner(itemOwner, byStartDescSorting).stream()
                    .filter(getFilterByState(state))
                    .collect(Collectors.toList());
        }
        return bookings;
    }

    @Override
    public Booking approveBooking(Long bookingId, Long itemOwnerId) {
        Booking booking = isItemOwner(itemOwnerId, bookingId);
        if (isBookingApproved(booking)) {
            throw new ValidationException(String.format("Booking %d is already approved.", bookingId));
        }
        booking.setStatus(Status.APPROVED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking rejectBooking(Long bookingId, Long itemOwnerId) {
        Booking booking = isItemOwner(itemOwnerId, bookingId);
        if (isBookingApproved(booking)) {
            throw new ValidationException(String.format("Booking %d is already approved.", bookingId));
        }
        booking.setStatus(Status.REJECTED);
        return bookingRepository.save(booking);
    }
}