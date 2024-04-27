package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.comment.dto.OutgoingCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import static ru.practicum.shareit.comment.dto.CommentDtoMapper.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public OutgoingItemDto createItem(
            @Valid @RequestBody IncomingItemDto incomingItemDto,
            @RequestHeader(USER_ID_HEADER) Long owner
    ) {
        log.info("Request to create item.");
        try {
            Item item = toItem(incomingItemDto);
            return toOutgoingDto(itemService.createItem(item, owner));
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found.");
        }
    }

    @PatchMapping("/{itemId}")
    public OutgoingItemDto updateItem(
            @RequestBody IncomingItemDto incomingItemDto,
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to update item {}.", itemId);
        try {
            Item item = itemService.getItem(itemId);
            partialMapToItem(incomingItemDto, item);
            return toOutgoingDto(itemService.updateItem(item, user));
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Item not found");
        }
    }

    @GetMapping("/{itemId}")
    public OutgoingItemDto getItemById(
        @PathVariable Long itemId,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to get item {}.", itemId);
        return itemService.getItemWithCommentsAndBookingsById(itemId, user);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        log.info("Request to delete item {}.", itemId);
        itemService.deleteItemById(itemId);
    }

    @GetMapping
    public List<OutgoingItemDto> getUserItems(@RequestHeader(USER_ID_HEADER) Long owner) {
        log.info("Request to receive user {}' items.", owner);
        return itemService.getUserItems(owner);
    }

    @GetMapping("/search")
    public List<OutgoingItemDto> search(@RequestParam String text) {
        log.info("Request to search items by \"{}\".", text);
        return toOutgoingDtoList(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public OutgoingCommentDto createComment(
        @RequestBody @Valid IncomingCommentDto incomingCommentDto,
        @PathVariable Long itemId,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        try {
            incomingCommentDto.setCreated(LocalDateTime.now());
            log.info("Request to create comment.");
            User user = userService.getUserById(userId);
            Comment comment = toComment(incomingCommentDto);
            comment.setUser(user);
            return toOutgoingDto(itemService.createComment(comment, itemId, userId));
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found.");
        }
    }
}