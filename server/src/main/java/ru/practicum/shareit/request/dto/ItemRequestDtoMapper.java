package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestDtoMapper {
    public static ItemRequest toItemRequest(IncomingItemRequestDto dto) {
        return ItemRequest.builder()
            .description(dto.getDescription())
            .build();
    }

    public OutgoingItemRequestDto toOutgoingDto(ItemRequest itemRequest) {
        return OutgoingItemRequestDto.builder()
            .id(itemRequest.getId())
            .description(itemRequest.getDescription())
            .created(itemRequest.getCreated())
            .build();
    }

    public List<ItemRequest> toItemRequestList(List<IncomingItemRequestDto> dtos) {
        return dtos.stream()
            .map(ItemRequestDtoMapper::toItemRequest)
            .collect(Collectors.toList());
    }

    public List<OutgoingItemRequestDto> toOutgoingDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
            .map(ItemRequestDtoMapper::toOutgoingDto)
            .collect(Collectors.toList());
    }
}