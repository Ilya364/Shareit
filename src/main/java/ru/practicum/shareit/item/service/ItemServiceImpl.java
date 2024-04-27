package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.OutForItemBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BookingNoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private User getUserById(Long userId) {
        try {
            return userRepository.findById(userId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found.");
        }
    }

    private List<Comment> getCommentsByItem(Item item) {
        return commentRepository.findAllByItem(item);
    }

    private OutForItemBooking getLastBooking(List<OutForItemBooking> dtoBookings) {
        List<OutForItemBooking> allPastBookings = dtoBookings.stream()
            .filter(
                dto -> dto.getStart().isBefore(LocalDateTime.now()) &&
                    dto.getStatus().equals(Status.APPROVED)
            )
            .collect(Collectors.toList());
        if (allPastBookings.size() > 0) {
            return allPastBookings.get(allPastBookings.size() - 1);
        } else {
            return null;
        }
    }

    private OutForItemBooking getNextBooking(List<OutForItemBooking> dtoBookings) {
        dtoBookings = dtoBookings.stream()
            .filter(
                dtoBooking -> dtoBooking.getStart().isAfter(LocalDateTime.now()) &&
                    dtoBooking.getStatus().equals(Status.APPROVED)
            )
            .collect(Collectors.toList());
        if (dtoBookings.size() > 0) {
            return dtoBookings.get(0);
        } else {
            return null;
        }
    }

    private List<OutForItemBooking> getBookingsByItems(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItemInOrderByStart(List.of(item));
        return BookingDtoMapper.toOutForItemDtoList(bookings);
    }

    private Map<Item, List<Booking>> getBookingsByItems(List<Item> items) {
        List<Booking> bookings = bookingRepository.findAllByItemInOrderByStart(items);
        Map<Item, List<Booking>> bookingsByItems = new HashMap<>();
        for (Item item : items) {
            List<Booking> itemBookings = bookings.stream()
                .filter(booking -> booking.getItem().equals(item))
                .collect(Collectors.toList());
            bookingsByItems.put(item, itemBookings);
        }
        return bookingsByItems;
    }

    @Override
    public Item createItem(Item item, Long itemOwnerId) {
        item.setOwner(getUserById(itemOwnerId));
        return itemRepository.save(item);
    }

    @Override
    public OutgoingItemDto getItemById(Long id, Long user) {
        try {
            Item item = itemRepository.findById(id).orElseThrow();
            OutgoingItemDto dtoItem = ItemDtoMapper.toOutgoingDto(item);

            dtoItem.setComments(getCommentsByItem(item));

            if (Objects.equals(item.getOwner().getId(), user)) {
                List<OutForItemBooking> bookings = getBookingsByItems(item);
                dtoItem.setLastBooking(getLastBooking(bookings));
                dtoItem.setNextBooking(getNextBooking(bookings));
            }

            return dtoItem;
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Item not found.");
        }
    }

    @Override
    public Item getItem(Long id) {
        try {
            return itemRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Item not found.");
        }
    }

    @Override
    public Item updateItem(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new BookingNoAccessException("User " + userId + " is not owner of Item " + item.getId() + ".");
        }
        return itemRepository.save(item);
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<OutgoingItemDto> getUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        Map<Item, List<Booking>> bookingsByItems = getBookingsByItems(items);
        List<OutgoingItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            OutgoingItemDto dtoItem = ItemDtoMapper.toOutgoingDto(item);

            dtoItem.setComments(getCommentsByItem(item));

            if (Objects.equals(item.getOwner().getId(), userId)) {
                List<Booking> bookings = bookingsByItems.get(item);
                List<OutForItemBooking> outForItemBookings = BookingDtoMapper.toOutForItemDtoList(bookings);
                dtoItem.setLastBooking(getLastBooking(outForItemBookings));
                dtoItem.setNextBooking(getNextBooking(outForItemBookings));
            }

            itemDtos.add(dtoItem);
        }
        return itemDtos;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> allMatchesByName = itemRepository.findAllByNameContainingIgnoreCase(text);
        List<Item> allMatchesByDescription = itemRepository.findAllByDescriptionContainingIgnoreCase(text);
        List<Item> items = new ArrayList<>(allMatchesByName);
        items.addAll(allMatchesByDescription);
        return items.stream()
            .distinct()
            .filter(Item::getAvailable)
            .collect(Collectors.toList());
    }

    @Override
    public Comment createComment(Comment comment, Long itemId, Long userId) {
        User user = getUserById(userId);
        try {
            bookingRepository.findAllByBookerOrderByStartDesc(user).stream()
                .filter(
                    booking -> booking.getItem().getId().equals(itemId) &&
                        booking.getBooker().getId().equals(userId) &&
                        booking.getEnd().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals(
                            Status.APPROVED)
                )
                .findFirst()
                .orElseThrow();

            comment.setUser(user);
            comment.setItem(itemRepository.findById(itemId).orElseThrow());
            comment.setAuthorName(user.getName());
        } catch (NoSuchElementException e) {
            throw new ValidationException("User can't leave a comment.");
        }
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getItemComments(Long itemId) {
        return commentRepository.findAllByItem(getItem(itemId));
    }
}