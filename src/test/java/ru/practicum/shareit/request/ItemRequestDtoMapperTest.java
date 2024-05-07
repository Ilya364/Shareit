package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRequestDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .creator(new User())
            .description("desc")
            .build();
        OutgoingItemRequestDto expected = OutgoingItemRequestDto.builder()
            .id(1L)
            .created(request.getCreated())
            .description("desc")
            .build();

        OutgoingItemRequestDto actual = ItemRequestDtoMapper.toOutgoingDto(request);

        assertEquals(expected, actual);
    }

    @Test
    void toOutgoingDtoListTest() {
        ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .creator(new User())
            .description("desc")
            .build();
        List<OutgoingItemRequestDto> expected = List.of(
            OutgoingItemRequestDto.builder()
                .id(1L)
                .created(request.getCreated())
                .description("desc")
                .build()
        );

        List<OutgoingItemRequestDto> actual = ItemRequestDtoMapper.toOutgoingDtoList(List.of(request));

        assertIterableEquals(expected, actual);
    }

    @Test
    void toItemRequestTest() {
        IncomingItemRequestDto dto = IncomingItemRequestDto.builder()
            .description("desc")
            .build();
        ItemRequest expected = ItemRequest.builder()
            .description("desc")
            .build();

        ItemRequest actual = ItemRequestDtoMapper.toItemRequest(dto);

        assertEquals(expected, actual);
    }

    @Test
    void toItemRequestListTest() {
        IncomingItemRequestDto dto = IncomingItemRequestDto.builder()
            .description("desc")
            .build();
        List<ItemRequest> expected = List.of(
            ItemRequest.builder()
                .description("desc")
                .build()
        );

        List<ItemRequest> actual = ItemRequestDtoMapper.toItemRequestList(List.of(dto));

        assertIterableEquals(expected, actual);
    }
}