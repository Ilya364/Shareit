package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.IncomingUserDto;
import ru.practicum.shareit.user.dto.OutgoingUserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class UserDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@maul.ru")
            .build();
        OutgoingUserDto expected = OutgoingUserDto.builder()
            .id(1L)
            .name(user.getName())
            .email(user.getEmail())
            .build();

        OutgoingUserDto actual = UserDtoMapper.toOutgoingDto(user);

        assertEquals(expected, actual);
    }

    @Test
    void toOutgoingDtoListTest() {
        User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@maul.ru")
            .build();
        List<OutgoingUserDto> expected = List.of(
            OutgoingUserDto.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build()
        );

        List<OutgoingUserDto> actual = UserDtoMapper.toOutgoingDtoList(List.of(user));

        assertIterableEquals(expected, actual);
    }

    @Test
    void toUserTest() {
        IncomingUserDto dto = IncomingUserDto.builder()
            .id(1L)
            .name("name")
            .email("email@maul.ru")
            .build();
        User expected = User.builder()
            .id(1L)
            .name("name")
            .email("email@maul.ru")
            .build();

        User actual = UserDtoMapper.toUser(dto);

        assertEquals(expected, actual);
    }

    @Test
    void toUseListTest() {
        List<IncomingUserDto> dtos = List.of(
            IncomingUserDto.builder()
                .id(1L)
                .name("name")
                .email("email@maul.ru")
                .build()
        );
        List<User> expected = List.of(
            User.builder()
                .id(1L)
                .name("name")
                .email("email@maul.ru")
                .build()
        );

        List<User> actual = UserDtoMapper.toUserList(dtos);

        assertIterableEquals(expected, actual);
    }
}