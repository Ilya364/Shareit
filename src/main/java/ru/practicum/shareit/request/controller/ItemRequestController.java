package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toItemRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public OutgoingItemRequestDto createItemRequest(
        @Valid @RequestBody IncomingItemRequestDto dto,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        ItemRequest request = toItemRequest(dto);
        return service.createItemRequest(request, userId);
    }

    @GetMapping
    public List<OutgoingItemRequestDto> getUserRequests(
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<OutgoingItemRequestDto> getPaginatedRequests(
        @RequestParam(value = "from", required = false) Integer from,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        if (from == null || size == null) {
            return service.getAll(userId);
        }
        return service.getPaginatedRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public OutgoingItemRequestDto getItemRequest(
        @PathVariable("requestId") Long requestId,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        return service.getItemRequest(requestId, userId);
    }
}