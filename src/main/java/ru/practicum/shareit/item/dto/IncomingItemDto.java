package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class IncomingItemDto {
    private final Long id;
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @NotNull
    private Boolean available;
}