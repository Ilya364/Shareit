package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class ItemDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .request(
                ItemRequest.builder()
                    .id(1L)
                    .build()
            )
            .build();
        OutgoingItemDto expected = OutgoingItemDto.builder()
            .id(1L)
            .available(true)
            .description(item.getDescription())
            .name(item.getName())
            .requestId(1L)
            .build();

        OutgoingItemDto actual = ItemDtoMapper.toOutgoingDto(item);

        assertEquals(expected, actual);
    }

    @Test
    void toItemTest() {
        IncomingItemDto dto = IncomingItemDto.builder()
            .available(true)
            .name("name")
            .description("description")
            .build();
        Item expected = Item.builder()
            .available(true)
            .name(dto.getName())
            .description(dto.getDescription())
            .build();

        Item actual = ItemDtoMapper.toItem(dto);

        assertEquals(expected, actual);
    }

    @Test
    void toOutgoingDtoListTest() {
        Item item1 = Item.builder()
            .id(1L)
            .name("name1")
            .description("description1")
            .available(true)
            .request(
                ItemRequest.builder()
                    .id(1L)
                    .build()
            )
            .build();
        Item item2 = Item.builder()
            .id(2L)
            .name("name2")
            .description("description2")
            .available(true)
            .request(
                ItemRequest.builder()
                    .id(2L)
                    .build()
            )
            .build();
        OutgoingItemDto expected1 = OutgoingItemDto.builder()
            .id(1L)
            .available(true)
            .description(item1.getDescription())
            .name(item1.getName())
            .requestId(1L)
            .build();
        OutgoingItemDto expected2 = OutgoingItemDto.builder()
            .id(2L)
            .available(true)
            .description(item2.getDescription())
            .name(item2.getName())
            .requestId(2L)
            .build();
        List<Item> items = List.of(item1, item2);
        List<OutgoingItemDto> expected = List.of(expected1, expected2);

        List<OutgoingItemDto> actual = ItemDtoMapper.toOutgoingDtoList(List.of(item1, item2));

        assertIterableEquals(expected, actual);
    }

    @Test
    void toItemListTest() {
        IncomingItemDto dto1 = IncomingItemDto.builder()
            .available(true)
            .name("name1")
            .description("description1")
            .build();
        Item expected1 = Item.builder()
            .available(true)
            .name(dto1.getName())
            .description(dto1.getDescription())
            .build();
        IncomingItemDto dto2 = IncomingItemDto.builder()
            .available(true)
            .name("name2")
            .description("description2")
            .build();
        Item expected2 = Item.builder()
            .available(true)
            .name(dto2.getName())
            .description(dto2.getDescription())
            .build();
        List<IncomingItemDto> dtos = List.of(dto1, dto2);
        List<Item> expected = List.of(expected1, expected2);

        List<Item> actual = ItemDtoMapper.toItemList(dtos);

        assertIterableEquals(expected, actual);
    }
}