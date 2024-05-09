package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.OutgoingItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemSerializationTest {
    @Autowired
    private JacksonTester<OutgoingItemDto> toJsonTester;
    @Autowired
    private JacksonTester<IncomingItemDto> toDtoTester;

    @Test
    void dtoSerializationTest() throws IOException {
        OutgoingItemDto dto = OutgoingItemDto.builder()
            .id(1L)
            .name("name")
            .available(true)
            .description("desc")
            .build();

        JsonContent<OutgoingItemDto> result = toJsonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void dtoDeserializationTest() throws IOException {
        String json = "{ \"name\":\"name\", \"description\":\"desc\", \"available\":\"true\"}";
        IncomingItemDto expected = IncomingItemDto.builder()
            .name("name")
            .description("desc")
            .available(true)
            .build();

        IncomingItemDto dto = toDtoTester.parseObject(json);

        assertEquals(expected, dto);
    }
}