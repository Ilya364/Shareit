package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.*;
import ru.practicum.shareit.comment.dto.OutgoingCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.comment.dto.CommentDtoMapper.*;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public OutgoingItemDto createItem(
        @Valid @RequestBody IncomingItemDto dto,
        @RequestHeader(USER_ID_HEADER) Long owner
    ) {
        ItemRequest request = null;
        if (dto.getRequestId() != null) {
            request = itemRequestRepository.findById(dto.getRequestId()).orElseThrow(
                () -> new NotFoundException(String.format("Item request %d not found.", dto.getRequestId()))
            );
        }
        Item item = toItem(dto);
        item.setRequest(request);
        return toOutgoingDto(itemService.createItem(item, owner));
    }

    @PatchMapping("/{itemId}")
    public OutgoingItemDto updateItem(
        @RequestBody IncomingItemDto incomingItemDto,
        @PathVariable Long itemId,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to update item {}.", itemId);
        Item item = itemService.getItem(itemId);
        partialMapToItem(incomingItemDto, item);
        return toOutgoingDto(itemService.updateItem(item, user));
    }

    @GetMapping("/{itemId}")
    public OutgoingItemDto getItemById(
        @PathVariable Long itemId,
        @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to get item {}.", itemId);
        return itemService.getItemById(itemId, user);
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
        log.info("Request to create comment.");
        Comment comment = toComment(incomingCommentDto);
        return toOutgoingDto(itemService.createComment(comment, itemId, userId));
    }
}