package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository {
    Item createItem(Item item, Long owner);

    Item getItemById(Long id);

    Item updateItem(IncomingItemDto incomingItemDto, Long id);

    void deleteItemById(Long id);

    List<Item> getUserItems(Long userId);

    List<Item> search(String text);
}