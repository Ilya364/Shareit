package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IsNotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long owner
    ) {
        Item item = service.createItem(mapToItem(itemDto), owner);
        return mapToItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody ItemDto itemDto,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long user
    ) {
        Item item = service.getItemById(itemId);
        if (!Objects.equals(item.getOwner(), user)) {
            throw new IsNotOwnerException("User " + user + " is not owner of Item " + item.getId() + ".");
        }
        return mapToItemDto(service.updateItem(itemDto, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return mapToItemDto(service.getItemById(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        service.deleteItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long owner) {
        return mapToItemDtoList(service.getUserItems(owner));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return mapToItemDtoList(service.search(text));
    }
}