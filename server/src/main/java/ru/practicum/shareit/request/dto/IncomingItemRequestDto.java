package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncomingItemRequestDto {
    @JsonProperty("description")
    private String description;

    @JsonCreator
    public IncomingItemRequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}