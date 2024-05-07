package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongUserIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user1 = User.builder()
        .id(1L)
        .name("User1")
        .email("user1@example.com")
        .build();
    private final User user2 = User.builder()
        .id(2L)
        .name("User2")
        .email("user2@example.com")
        .build();
    private final Item item1 = Item.builder()
        .id(1L)
        .name("Item1")
        .description("Description1")
        .available(true)
        .owner(user1)
        .request(null)
        .build();
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .status(Status.WAITING)
            .booker(user2)
            .item(item1)
            .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class)))
            .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Booking result = bookingService.createBooking(booking, 2L);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void createBookingNotFoundItemTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.ofNullable(null));
        Booking forCreate = booking;
        forCreate.setItem(
            Item.builder()
                .id(3L)
                .build()
        );

        WrongUserIdException e = assertThrows(
            WrongUserIdException.class,
            () -> {
                bookingService.createBooking(forCreate, 2L);
            }
        );
        assertEquals("Item 3 is not exist.", e.getMessage());
    }

    @Test
    void createBookingNotAvailableItemTest() {
        Item notAvailableItem = Item.builder()
            .id(5L)
            .available(false)
            .owner(user1)
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(notAvailableItem));
        Booking forCreate = booking;
        forCreate.setItem(notAvailableItem);

        ValidationException e = assertThrows(
            ValidationException.class,
            () -> {
                bookingService.createBooking(forCreate, 2L);
            }
        );
        assertEquals("Item 5 is unavailable.", e.getMessage());
    }

    @Test
    void createBookingNotFoundBookerTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(booking, 1L));
    }

    @Test
    void deleteBookingByIdTest() {
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));

        bookingRepository.deleteById(1L);

        Mockito.verify(bookingRepository, Mockito.times(1))
            .deleteById(anyLong());
    }

    @Test
    void getBookingByBookerTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L, 2L);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingByItemOwnerTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L, 1L);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }


    @Test
    void getBookingNoAccessTest() {
        User user = User.builder()
            .id(3L)
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 3L));
    }

    @Test
    void getBookingsOfBookerTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBooker(user2, Sort.by(DESC, "start")))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getUserBookings(2L, State.ALL).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfBookerByWaitingState() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerAndStatus(user2, Status.WAITING, Sort.by(DESC, "start")))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getUserBookings(2L, State.WAITING).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsBookerNotFoundTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));

        assertThrows(WrongUserIdException.class, () -> bookingService.getUserBookings(1L, State.CURRENT));
    }

    @Test
    void getBookingsOfItemOwnerTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByItemOwner(user1, Sort.by(DESC, "start")))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getItemOwnerBookings(1L, State.ALL).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfItemOwnerByWaitingStateTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByItemOwnerAndStatus(user1, Status.WAITING, Sort.by(DESC, "start")))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getItemOwnerBookings(1L, State.WAITING).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfItemOwnerByCurrentStateTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByItemOwner(user1, Sort.by(DESC, "start")))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getItemOwnerBookings(1L, State.CURRENT).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOwnerNotFoundTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));

        assertThrows(WrongUserIdException.class, () -> bookingService.getItemOwnerBookings(1L, State.CURRENT));
    }

    @Test
    void getBookingsOfBookerWithPaginationTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        Pageable pageable = PageRequest.of(0, 1, Sort.by(DESC, "start"));
        when(bookingRepository.findAllByBooker(user2, pageable))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getUserBookings(1L, State.ALL, 0, 1).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfBookerWithPaginationByWaitingStateTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        Pageable pageable = PageRequest.of(0, 1, Sort.by(DESC, "start"));
        when(bookingRepository.findAllByBookerAndStatus(user2, Status.WAITING, pageable))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getUserBookings(1L, State.WAITING, 0, 1).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfBookerWithPaginationByCurrentStateTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));
        Pageable pageable = PageRequest.of(0, 1, Sort.by(DESC, "start"));
        when(bookingRepository.findAllByBooker(user2, pageable))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getUserBookings(1L, State.CURRENT, 0, 1).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfBookerNotValidPaginationFoundTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(2L, State.ALL, 0, -1));
    }

    @Test
    void getBookingsOfItemOwnerWithPaginationTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        Pageable pageable = PageRequest.of(0, 1, Sort.by(DESC, "start"));
        when(bookingRepository.findAllByItemOwner(user1, pageable))
            .thenReturn(List.of(booking));

        Booking result = bookingService.getItemOwnerBookings(1L, State.ALL, 0, 1).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfOwnerNotValidPaginationFoundTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.getItemOwnerBookings(2L, State.ALL, 0, -1));
    }

    @Test
    void approveBookingTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
            .thenReturn(Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(Status.APPROVED)
                .build());

        Booking result = bookingService.approveBooking(booking.getId(), user1.getId());

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approveBookingAlreadyApproveTest() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L));
    }

    @Test
    void approveBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 2L));
    }

    @Test
    void rejectBookingTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.save(any()))
            .thenReturn(booking);

        Booking result = bookingService.rejectBooking(booking.getId(), user1.getId());

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void rejectBookingAlreadyApproveTest() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> bookingService.rejectBooking(1L, 1L));
    }

    @Test
    void rejectBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.rejectBooking(1L, 2L));
    }
}