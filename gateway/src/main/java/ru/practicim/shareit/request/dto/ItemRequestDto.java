package ru.practicim.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemRequestDto {
    @NotNull
    @NotEmpty
    @JsonProperty("description")
    private String description;

    @JsonCreator
    public ItemRequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}
