package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutForItemBooking;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class BookingDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(new Item())
            .booker(new User())
            .status(Status.APPROVED)
            .build();
        OutgoingBookingDto expectedDto = OutgoingBookingDto.builder()
            .id(1L)
            .start(booking.getStart())
            .end(booking.getEnd())
            .item(new Item())
            .booker(new User())
            .status(Status.APPROVED)
            .build();

        OutgoingBookingDto actualDto = BookingDtoMapper.toOutgoingDto(booking);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void toBookingTest() {
        IncomingBookingDto bookingDto = IncomingBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .build();

        Booking actualBooking = BookingDtoMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
    }

    @Test
    void toBookingForItemDtoTest() {
        User booker = User.builder()
            .id(1L)
            .name("name")
            .email("ilya@mail.ru")
            .build();
        Item item = Item.builder()
            .id(1L)
            .name("itemName")
            .description("description")
            .owner(new User())
            .available(true)
            .build();
        Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(item)
            .booker(booker)
            .status(Status.APPROVED)
            .build();
        OutForItemBooking expectedDto = OutForItemBooking.builder()
            .id(1L)
            .start(booking.getStart())
            .end(booking.getEnd())
            .itemId(1L)
            .bookerId(1L)
            .status(Status.APPROVED)
            .build();

        OutForItemBooking actualDto = BookingDtoMapper.toOutForItemDto(booking);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void toOutgoingDtoListTest() {
        Booking booking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(new Item())
            .booker(new User())
            .status(Status.APPROVED)
            .build();
        Booking booking2 = Booking.builder()
            .id(2L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2))
            .item(new Item())
            .booker(new User())
            .status(Status.REJECTED)
            .build();
        List<Booking> bookings = List.of(booking1, booking2);
        OutgoingBookingDto expectedDto1 = OutgoingBookingDto.builder()
            .id(1L)
            .start(booking1.getStart())
            .end(booking1.getEnd())
            .item(new Item())
            .booker(new User())
            .status(Status.APPROVED)
            .build();
        OutgoingBookingDto expectedDto2 = OutgoingBookingDto.builder()
            .id(2L)
            .start(booking2.getStart())
            .end(booking2.getEnd())
            .item(new Item())
            .booker(new User())
            .status(Status.REJECTED)
            .build();
        List<OutgoingBookingDto> expectedDtos = List.of(expectedDto1, expectedDto2);

        List<OutgoingBookingDto> actualDtos = BookingDtoMapper.toOutgoingDtoList(bookings);

        assertIterableEquals(expectedDtos, actualDtos);
    }

    @Test
    void toBookingListTest() {
        IncomingBookingDto dto1 = IncomingBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .build();
        IncomingBookingDto dto2 = IncomingBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2))
            .build();
        Booking expectedBooking1 = Booking.builder()
            .start(dto1.getStart())
            .end(dto1.getEnd())
            .status(Status.WAITING)
            .build();
        Booking expectedBooking2 = Booking.builder()
            .start(dto2.getStart())
            .end(dto2.getEnd())
            .status(Status.WAITING)
            .build();
        List<IncomingBookingDto> dtos = List.of(dto1, dto2);

        List<Booking> expectedBookings = List.of(expectedBooking1, expectedBooking2);

        List<Booking> actualBookings = BookingDtoMapper.toBookingList(dtos);

        assertIterableEquals(expectedBookings, actualBookings);
    }

    @Test
    void toBookingForItemListTest() {
        Booking booking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(
                Item.builder()
                    .id(1L)
                    .build()
            )
            .booker(
                User.builder()
                    .id(1L)
                    .build()
            )
            .status(Status.APPROVED)
            .build();
        Booking booking2 = Booking.builder()
            .id(2L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2))
            .item(
                Item.builder()
                    .id(2L)
                    .build()
            )
            .booker(
                User.builder()
                    .id(2L)
                    .build()
            )
            .status(Status.REJECTED)
            .build();
        List<Booking> bookings = List.of(booking1, booking2);

        OutForItemBooking expectedDto1 = OutForItemBooking.builder()
            .id(1L)
            .start(booking1.getStart())
            .end(booking1.getEnd())
            .itemId(1L)
            .bookerId(1L)
            .status(Status.APPROVED)
            .build();
        OutForItemBooking expectedDto2 = OutForItemBooking.builder()
            .id(2L)
            .start(booking2.getStart())
            .end(booking2.getEnd())
            .itemId(2L)
            .bookerId(2L)
            .status(Status.REJECTED)
            .build();
        List<OutForItemBooking> expectedDtos = List.of(expectedDto1, expectedDto2);

        List<OutForItemBooking> actualDtos = BookingDtoMapper.toOutForItemDtoList(bookings);

        assertIterableEquals(expectedDtos, actualDtos);
    }
}