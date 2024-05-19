package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemDtoMapper {
    public OutgoingItemDto toOutgoingDto(Item item) {
        OutgoingItemDto dto = OutgoingItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .build();
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
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
        Field[] dtoFields = dto.getClass().getDeclaredFields();
        Class<? extends Item> itemClass = item.getClass();
        for (Field dtoField: dtoFields) {
            dtoField.setAccessible(true);
            try {
                if (dtoField.get(dto) == null) {
                    continue;
                }

                String fieldName = dtoField.getName();

                Field itemField = itemClass.getDeclaredField(fieldName);
                itemField.setAccessible(true);
                itemField.set(item, dtoField.get(dto));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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