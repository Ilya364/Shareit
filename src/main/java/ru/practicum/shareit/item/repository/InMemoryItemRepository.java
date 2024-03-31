package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Item createItem(Item item, Long owner) {
        item.setOwner(owner);
        item.setId(nextId++);
        items.put(item.getId(), item);
        log.info("Item {} is created.", item.getId());
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Item " + id + " is not found.");
        }
        log.info("Item {} is received.", item.getId());
        return item;
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long id) {
        Item item = items.get(id);
        ItemDtoMapper.partialMapToItem(itemDto, item);
        log.info("Item {} is updated.", id);
        return getItemById(id);
    }

    @Override
    public void deleteItemById(Long id) {
        items.remove(id);
        log.info("Item {} is deleted.", id);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        List<Item> userItems = items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
        log.info("List of User {} items is received.", userId);
        return userItems;
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> containsTextCaseInsensitive(item, text) && item.getAvailable())
                .collect(Collectors.toList());
    }

    private boolean containsTextCaseInsensitive(Item item, String text) {
        boolean contains = false;
        if (item.getName().toLowerCase().contains(text.toLowerCase())) {
            contains = true;
        }
        if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
            contains = true;
        }
        if (text.isEmpty()) {
            contains = false;
        }
        return contains;
    }
}