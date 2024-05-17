package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    OutgoingItemRequestDto createItemRequest(ItemRequest itemRequest, Long creator);

    List<OutgoingItemRequestDto> getUserRequests(Long userId);

    List<OutgoingItemRequestDto> getPaginatedRequests(Long userId, Integer from, Integer size);

    OutgoingItemRequestDto getItemRequest(Long requestId, Long userId);

    List<OutgoingItemRequestDto> getAll(Long userId);
}