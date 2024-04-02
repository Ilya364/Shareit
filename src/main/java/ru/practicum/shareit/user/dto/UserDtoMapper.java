package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserDtoMapper {
    public OutgoingUserDto toOutgoingDto(User user) {
        return OutgoingUserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public User toUser(IncomingUserDto dto) {
        return User.builder()
            .id(dto.getId())
            .name(dto.getName())
            .email(dto.getEmail())
            .build();
    }

    public List<OutgoingUserDto> toOutgoingDtoList(List<User> users) {
        return users.stream()
                .map(UserDtoMapper::toOutgoingDto)
                .collect(Collectors.toList());
    }

    public List<User> toUserList(List<IncomingUserDto> incomingUserDtos) {
        return incomingUserDtos.stream()
                .map(UserDtoMapper::toUser)
                .collect(Collectors.toList());
    }
}