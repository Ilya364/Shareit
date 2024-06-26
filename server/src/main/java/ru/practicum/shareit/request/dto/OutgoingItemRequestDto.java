package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OutgoingItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<OutgoingItemDto> items;
}