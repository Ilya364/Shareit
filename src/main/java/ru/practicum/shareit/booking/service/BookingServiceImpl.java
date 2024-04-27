package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongUserIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Comparator<Booking> bookingByStartComparator = Comparator.comparing(Booking::getStart).reversed();

    private boolean isApproved(Booking booking) {
        if (booking.getStatus().equals(Status.APPROVED)) {
            return true;
        } else {
            return false;
        }
    }

    private Booking isBooker(Long userId, Long bookingId) {
        Booking booking = null;
        try {
            booking = bookingRepository.findById(bookingId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Booking " + bookingId + " is not found.");
        }
        boolean isUserBooker = booking.getBooker().getId().equals(userId);
        if (isUserBooker) {
            return booking;
        } else {
            throw new NotFoundException(userId + " doesn't have access.");
        }
    }

    private Booking isItemOwner(Long userId, Long bookingId) {
        Booking booking = null;
        try {
            booking = bookingRepository.findById(bookingId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Booking " + bookingId + " is not found.");
        }
        boolean isUserItemOwner = booking.getItem().getOwner().getId().equals(userId);
        if (isUserItemOwner) {
            return booking;
        } else {
            throw new NotFoundException(userId + " doesn't have access.");
        }
    }

    private Booking checkUserHasAccess(Long bookingId, Long userId) {
        Booking booking = null;
        try {
            booking = isBooker(userId, bookingId);
        } catch (NotFoundException e) {
            booking = isItemOwner(userId, bookingId);
        }
        return booking;
    }

    @Override
    public Booking createBooking(Booking booking, Long owner) {
        User user;
        try {
            user = userRepository.findById(owner).orElseThrow();
            Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow();
            if (item.getOwner().getId().equals(owner)) {
                throw new NotFoundException("");
            }
        } catch (NoSuchElementException e) {
            throw new WrongUserIdException("User " + owner + " is not exist.");
        }
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        return checkUserHasAccess(bookingId, userId);
    }

    @Override
    public void deleteBookingById(Long bookingId, Long userId) {
        isBooker(userId, bookingId);
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public List<Booking> getUserBookings(Long userId, State state) {
        User booker;
        try {
            booker = userRepository.findById(userId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found.");
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(booker).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker(booker).stream()
                    .sorted(bookingByStartComparator)
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker(booker).stream()
                    .sorted(bookingByStartComparator)
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker(booker).stream()
                    .sorted(bookingByStartComparator)
                    .filter(booking ->
                        booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())
                    )
                    .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatus(booker, Status.WAITING).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatus(booker, Status.REJECTED).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            case CANCELLED:
                bookings = bookingRepository.findAllByBookerAndStatus(booker, Status.CANCELLED).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            default:
                throw new RuntimeException();
        }
        return bookings;
    }

    @Override
    public List<Booking> getItemOwnerBookings(Long ownerId, State state) {
        User owner;
        try {
            owner = userRepository.findById(ownerId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found.");
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(owner).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwner(owner).stream()
                    .sorted(bookingByStartComparator)
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwner(owner).stream()
                    .sorted(bookingByStartComparator)
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwner(owner).stream()
                    .sorted(bookingByStartComparator)
                    .filter(booking ->
                        booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())
                    )
                    .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.WAITING).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.REJECTED).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            case CANCELLED:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.CANCELLED).stream()
                    .sorted(bookingByStartComparator)
                    .collect(Collectors.toList());
                break;
            default:
                throw new RuntimeException();
        }
        return bookings;
    }

    @Override
    public Booking approveBooking(Long bookingId, Long itemOwnerId) {
        Booking booking = isItemOwner(itemOwnerId, bookingId);
        if (isApproved(booking)) {
            throw new ValidationException("already approved");
        }
        booking.setStatus(Status.APPROVED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking rejectBooking(Long bookingId, Long itemOwnerId) {
        Booking booking = isItemOwner(itemOwnerId, bookingId);
        if (isApproved(booking)) {
            throw new ValidationException("already approved");
        }
        booking.setStatus(Status.REJECTED);
        return bookingRepository.save(booking);
    }
}