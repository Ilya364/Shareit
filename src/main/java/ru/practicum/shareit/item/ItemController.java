package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public OutgoingItemDto createItem(
            @Valid @RequestBody IncomingItemDto incomingItemDto,
            @RequestHeader(USER_ID_HEADER) Long owner
    ) {
        log.info("Request to create item.");
        Item item = service.createItem(toItem(incomingItemDto), owner);
        return toOutgoingDto(item);
    }

    @PatchMapping("/{itemId}")
    public OutgoingItemDto updateItem(
            @RequestBody IncomingItemDto incomingItemDto,
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to update item {}.", itemId);
        return toOutgoingDto(service.updateItem(incomingItemDto, itemId, user));
    }

    @GetMapping("/{itemId}")
    public OutgoingItemDto getItemById(@PathVariable Long itemId) {
        log.info("Request to get item {}.", itemId);
        return toOutgoingDto(service.getItemById(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        log.info("Request to delete item {}.", itemId);
        service.deleteItemById(itemId);
    }

    @GetMapping
    public List<OutgoingItemDto> getUserItems(@RequestHeader(USER_ID_HEADER) Long owner) {
        log.info("Request to receive user {}' items.", owner);
        return toOutgoingDtoList(service.getUserItems(owner));
    }

    @GetMapping("/search")
    public List<OutgoingItemDto> search(@RequestParam String text) {
        log.info("Request to search items by \"{}\".", text);
        return toOutgoingDtoList(service.search(text));
    }
}