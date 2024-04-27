package ru.practicum.shareit.booking.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Primary
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker); //TODO - 1.Как-то проверку на время бронирования
    List<Booking> findAllByBookerAndStatus(User booker, Status status);
    List<Booking> findAllByItemOwner(User itemOwner); //TODO - 2.Как-то проверку на время бронирования
    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, Status status);
    List<Booking> findAllByItemInOrderByStart(List<Item> items);
}