package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class IncomingUserDto {
    private final Long id;
    @NotBlank
    private final String name;
    @Email
    @NotNull
    private final String email;
}