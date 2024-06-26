package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final Sort byStartBookingDescSorting = Sort.by(Sort.Direction.DESC, "start");
    private final Sort byStartBookingAscSorting = Sort.by(Sort.Direction.ASC, "start");
    private final Sort byIdItemAscSorting = Sort.by(Sort.Direction.ASC, "id");

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException(String.format("User %d is not found.", userId))
        );
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
        if (!allPastBookings.isEmpty()) {
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
        if (!dtoBookings.isEmpty()) {
            return dtoBookings.get(0);
        } else {
            return null;
        }
    }

    private List<OutForItemBooking> getBookingsByItems(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItemIn(List.of(item), byStartBookingAscSorting);
        return BookingDtoMapper.toOutForItemDtoList(bookings);
    }

    private Map<Item, List<Booking>> getBookingsByItems(List<Item> items) {
        List<Booking> bookings = bookingRepository.findAllByItemIn(items, byStartBookingAscSorting);
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
    @Transactional(readOnly = true)
    public OutgoingItemDto getItemById(Long itemId, Long user) {
        Item item = itemRepository.findById(itemId).orElseThrow(
            () -> new NotFoundException(String.format("Item %d is not found.", itemId))
        );
        OutgoingItemDto dtoItem = ItemDtoMapper.toOutgoingDto(item);

        dtoItem.setComments(getCommentsByItem(item));

        if (Objects.equals(item.getOwner().getId(), user)) {
            List<OutForItemBooking> bookings = getBookingsByItems(item);
            dtoItem.setLastBooking(getLastBooking(bookings));
            dtoItem.setNextBooking(getNextBooking(bookings));
        }
        return dtoItem;
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
            () -> new NotFoundException(String.format("Item %d is not found.", itemId))
        );
    }

    @Override
    public Item updateItem(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new BookingNoAccessException(
                String.format("User %d is not owner of Item %d.", userId, item.getId())
            );
        }
        return itemRepository.save(item);
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutgoingItemDto> getUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, byIdItemAscSorting);
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
    @Transactional(readOnly = true)
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
        bookingRepository.findAllByBooker(user, byStartBookingDescSorting).stream()
            .filter(
                booking -> booking.getItem().getId().equals(itemId) &&
                    booking.getBooker().getId().equals(userId) &&
                    booking.getEnd().isBefore(LocalDateTime.now()) &&
                    booking.getStatus().equals(
                        Status.APPROVED)
            )
            .findFirst()
            .orElseThrow(
                () -> new ValidationException(
                    String.format("User %d can't leave a comment for Item %d.", userId, itemId)
                )
            );

        comment.setUser(user);
        comment.setItem(itemRepository.findById(itemId).orElseThrow(
            () -> new NotFoundException(String.format("Item %d is not found.", itemId))
        ));
        comment.setAuthorName(user.getName());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getItemComments(Long itemId) {
        return commentRepository.findAllByItem(getItem(itemId));
    }
}