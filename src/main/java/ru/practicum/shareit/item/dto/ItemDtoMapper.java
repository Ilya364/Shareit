package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.lang.reflect.Field;
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
/*        if (dto.getName() != null) {
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
        }*/

        Field[] dtoFields = dto.getClass().getDeclaredFields();
        Class<? extends Item> itemClass = item.getClass();
        for (Field dtoField: dtoFields) {
            dtoField.setAccessible(true);
            try {
                if (dtoField.get(dto) == null) {
                    continue;
                }
                String fieldName = dtoField.getName();
                if (fieldName.equals("name") || fieldName.equals("description")) {
                    String fieldValue = (String)dtoField.get(dto);
                    if (fieldValue.isEmpty()) {
                        throw new ValidationException(fieldName + " can't be empty.");
                    }
                }

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