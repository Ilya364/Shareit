package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemDtoMapper {
    public OutgoingItemDto toOutgoingDto(Item item) {
        return OutgoingItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .build();
    }

    public Item toItem(IncomingItemDto dto) {
        return Item.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .available(dto.getAvailable())
            .build();
    }

    public void partialMapToItem(IncomingItemDto dto, Item item) {
        if (dto.getName() != null) {
            if (!dto.getName().isEmpty()) {
                item.setName(dto.getName());
            } else {
                throw new ValidationException("Name can't be empty.");
            }
        }
        if (dto.getDescription() != null) {
            if (!dto.getDescription().isEmpty()) {
                item.setDescription(dto.getDescription());
            } else {
                throw new ValidationException("Description can't be empty.");
            }
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
    }

    public List<OutgoingItemDto> toOutgoingDtoList(List<Item> items) {
        return items.stream()
                .map(ItemDtoMapper::toOutgoingDto)
                .collect(Collectors.toList());
    }

    public List<Item> toItemList(List<IncomingItemDto> dtos) {
        return dtos.stream()
                .map(ItemDtoMapper::toItem)
                .collect(Collectors.toList());
    }
}