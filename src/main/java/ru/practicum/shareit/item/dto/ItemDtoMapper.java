package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemDtoMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item mapToItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static void partialMapToItem(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            if (!itemDto.getName().isEmpty()) {
                item.setName(itemDto.getName());
            } else {
                throw new ValidationException("Name can't be empty.");
            }
        }
        if (itemDto.getDescription() != null) {
            if (!itemDto.getDescription().isEmpty()) {
                item.setDescription(itemDto.getDescription());
            } else {
                throw new ValidationException("Description can't be empty.");
            }
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    public static List<ItemDto> mapToItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    public static List<Item> mapToItemList(List<ItemDto> itemDtos) {
        return itemDtos.stream()
                .map(ItemDtoMapper::mapToItem)
                .collect(Collectors.toList());
    }
}