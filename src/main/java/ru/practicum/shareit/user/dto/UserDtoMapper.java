package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserDtoMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User mapToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static List<UserDto> mapToUserDtoList(List<User> users) {
        return users.stream()
                .map(UserDtoMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public static List<User> mapToUserList(List<UserDto> userDtos) {
        return userDtos.stream()
                .map(UserDtoMapper::mapToUser)
                .collect(Collectors.toList());
    }
}