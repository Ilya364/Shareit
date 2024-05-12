package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class IncomingItemRequestDto {
    @NotEmpty
    @JsonProperty("description")
    private String description;

    @JsonCreator
    public IncomingItemRequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }

}