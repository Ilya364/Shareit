package ru.practicim.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicim.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient client;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
        @Valid @RequestBody ItemRequestDto dto,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to create item request.");
        return client.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to get item requests.");
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPaginatedRequests(
        @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
        @Positive @RequestParam(value = "size", required = false) Integer size,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request get all item requests.");
        if (from != null && size != null) {
            return client.getPaginatedRequests(userId, from, size);
        }
        return client.getAll(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(
        @PathVariable("requestId") Long requestId,
        @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to get item request.");
        return client.getById(userId, requestId);
    }
}
