package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.IncomingUserDto;
import ru.practicum.shareit.user.dto.OutgoingUserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserSerializationTest {
    @Autowired
    private JacksonTester<OutgoingUserDto> toJsonTester;
    @Autowired
    private JacksonTester<IncomingUserDto> toDtoTester;

    @Test
    void dtoSerializationTest() throws IOException {
        OutgoingUserDto userDto = OutgoingUserDto.builder()
            .id(1L)
            .email("john.doe@mail.com")
            .name("John")
            .build();

        JsonContent<OutgoingUserDto> result = toJsonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    void dtoDeserializationTest() throws IOException {
        String json = "{ \"name\":\"name\", \"email\":\"email@mail.ru\"}";
        IncomingUserDto expected = IncomingUserDto.builder()
            .name("name")
            .email("email@mail.ru")
            .build();

        IncomingUserDto dto = toDtoTester.parseObject(json);

        assertEquals(expected, dto);
    }
}