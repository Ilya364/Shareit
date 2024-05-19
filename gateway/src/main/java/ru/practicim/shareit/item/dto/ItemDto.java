package ru.practicim.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class ItemDto {
    private final Long id;
    @NotEmpty
    @NotNull
    private final String name;
    @NotEmpty
    @NotNull
    private final String description;
    @NotNull
    private Boolean available;
    @Positive
    private Long owner;
    @Positive
    private Long requestId;
}
