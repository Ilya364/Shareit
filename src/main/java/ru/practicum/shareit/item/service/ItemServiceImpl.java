package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.OutForItemBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.comment.dto.OutgoingCommentDto;
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
    private final Comparator<OutForItemBooking> bookingByStartComparator = Comparator.comparing(OutForItemBooking::getStart);

    @Override
    public Item createItem(Item item, Long owner) {
        try {
            item.setOwner(userRepository.findById(owner).orElseThrow());
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found.");
        }
        return itemRepository.save(item);
    }

    @Override
    public OutgoingItemDto getItemWithCommentsAndBookingsById(Long id, Long user) {
        try {
            Item item = itemRepository.findById(id).orElseThrow();
            OutgoingItemDto dtoItem = ItemDtoMapper.toOutgoingDto(item);

            List<Comment> comments = commentRepository.findAllByItem(item);
            dtoItem.setComments(comments);
            List<OutgoingCommentDto> dtoComments = CommentDtoMapper.toOutgoingDtoList(comments);

            if (Objects.equals(item.getOwner().getId(), user)) {
                List<Booking> bookings = bookingRepository.findAllByItemInOrderByStart(List.of(item));
                List<OutForItemBooking> dtoBookings = BookingDtoMapper.toOutForItemDtoList(bookings);
                List<OutForItemBooking> lastDtos = dtoBookings.stream()
                    .filter(
                        dto -> dto.getStart().isBefore(LocalDateTime.now())&&
                        dto.getStatus().equals(Status.APPROVED)
                    )
                    .collect(Collectors.toList());
                if (lastDtos.size() > 0) {
                    dtoItem.setLastBooking(lastDtos.get(lastDtos.size() - 1));
                }
                dtoBookings = dtoBookings.stream()
                    .filter(
                        dtoBooking -> dtoBooking.getStart().isAfter(LocalDateTime.now())&&
                        dtoBooking.getStatus().equals(Status.APPROVED)
                    )
                    .collect(Collectors.toList());
                if (dtoBookings.size() > 0) {
                    dtoItem.setNextBooking(dtoBookings.get(0));
                }
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
        List<OutgoingItemDto> itemDtos = new ArrayList<>();
        for (Item item: items) {
            OutgoingItemDto dtoItem = ItemDtoMapper.toOutgoingDto(item);

            List<Comment> comments = commentRepository.findAllByItem(item);
            dtoItem.setComments(comments);
            List<OutgoingCommentDto> dtoComments = CommentDtoMapper.toOutgoingDtoList(comments);

            if (Objects.equals(item.getOwner().getId(), userId)) {
                List<Booking> bookings = bookingRepository.findAllByItemInOrderByStart(List.of(item));
                List<OutForItemBooking> dtoBookings = BookingDtoMapper.toOutForItemDtoList(bookings);
                List<OutForItemBooking> lastDtos = dtoBookings.stream()
                    .filter(
                        dto -> dto.getStart().isBefore(LocalDateTime.now()) &&
                        dto.getStatus().equals(Status.APPROVED)
                    )
                    .collect(Collectors.toList());
                if (lastDtos.size() > 0) {
                    dtoItem.setLastBooking(lastDtos.get(lastDtos.size() - 1));
                }
                dtoBookings = dtoBookings.stream()
                    .filter(
                        dtoBooking -> dtoBooking.getStart().isAfter(LocalDateTime.now())  &&
                        dtoBooking.getStatus().equals(Status.APPROVED)
                    )
                    .collect(Collectors.toList());
                if (dtoBookings.size() > 0) {
                    dtoItem.setNextBooking(dtoBookings.get(0));
                }
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
        User user;
        try {
            try {
                user = userRepository.findById(userId).orElseThrow();
            } catch (NoSuchElementException e) {
                throw new NotFoundException("User not found");
            }
            bookingRepository.findAllByBooker(user).stream()
                .filter(
                    booking -> booking.getItem().getId().equals(itemId) &&
                    booking.getEnd().isBefore(LocalDateTime.now()) &&
                    booking.getStatus().equals(
                    Status.APPROVED)
                )
                .findFirst()
                .orElseThrow();//TODO - 3.Вот тут криво, эксепшн один и тот же на оба случая - исправь
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