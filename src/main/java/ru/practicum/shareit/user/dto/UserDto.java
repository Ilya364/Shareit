package ru.practicum.shareit.user.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private final Long id;
    @NotBlank
    private final String name;
    @Email
    @NotNull
    private final String email;
}