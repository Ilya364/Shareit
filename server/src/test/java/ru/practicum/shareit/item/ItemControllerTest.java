package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.BookingNoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    private IncomingItemDto incomingItemDto;
    private Item item;

    @SneakyThrows
    @Test
    void createItemTest() {
        incomingItemDto = IncomingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
        item = ItemDtoMapper.toItem(incomingItemDto);
        when(itemService.createItem(any(Item.class), any()))
            .thenReturn(item);

        mvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(incomingItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(item.getName())))
            .andExpect(jsonPath("$.description", is(item.getDescription())))
            .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void createItemNotFoundRequestTest() {
        ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("description")
            .build();
        incomingItemDto = IncomingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .requestId(2L)
            .build();
        item = ItemDtoMapper.toItem(incomingItemDto);
        when(itemService.createItem(any(Item.class), any()))
            .thenReturn(item);
        when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.ofNullable(null));

        mvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(incomingItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        incomingItemDto = IncomingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
        item = ItemDtoMapper.toItem(incomingItemDto);

        when(itemService.updateItem(any(Item.class), anyLong()))
            .thenReturn(item);
        when(itemService.getItem(anyLong()))
            .thenReturn(item);

        mvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(incomingItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(item.getName())))
            .andExpect(jsonPath("$.description", is(item.getDescription())))
            .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void updateItemNotFoundTest() {
        incomingItemDto = IncomingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
        item = ItemDtoMapper.toItem(incomingItemDto);

        when(itemService.updateItem(any(Item.class), anyLong()))
            .thenReturn(item);
        when(itemService.getItem(anyLong()))
            .thenAnswer(invocationOnMock -> {
                throw new NotFoundException("");
            });

        mvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(incomingItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateItemNoAccessTest() {
        incomingItemDto = IncomingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
        item = ItemDtoMapper.toItem(incomingItemDto);

        when(itemService.updateItem(any(Item.class), anyLong()))
            .thenThrow(BookingNoAccessException.class);
        when(itemService.getItem(anyLong()))
            .thenAnswer(invocationOnMock -> {
                throw new BookingNoAccessException("");
            });

        mvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(incomingItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    void updateItemValidationFailedTest() {
        incomingItemDto = IncomingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
        item = ItemDtoMapper.toItem(incomingItemDto);

        when(itemService.updateItem(any(Item.class), anyLong()))
            .thenThrow(BookingNoAccessException.class);
        when(itemService.getItem(anyLong()))
            .thenAnswer(invocationOnMock -> {
                throw new ValidationException("");
            });

        mvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(incomingItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getItemTest() {
        item = Item.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();
        when(itemService.getItemById(anyLong(), anyLong()))
            .thenReturn(ItemDtoMapper.toOutgoingDto(item));

        mvc.perform(get("/items/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(item.getName())))
            .andExpect(jsonPath("$.description", is(item.getDescription())))
            .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    @SneakyThrows
    void deleteItemTest() {
        mvc.perform(delete("/items/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk());

        verify(itemService, Mockito.times(1))
            .deleteItemById(1L);
    }

    @Test
    @SneakyThrows
    void getUserItems() {
        item = Item.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();
        when(itemService.getUserItems(anyLong()))
            .thenReturn(List.of(ItemDtoMapper.toOutgoingDto(item)));

        mvc.perform(get("/items")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
            .andExpect(jsonPath("$.[0].name", is(item.getName())))
            .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
            .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));
    }

    @Test
    @SneakyThrows
    void searchItems() {
        item = Item.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();
        when(itemService.search(anyString()))
            .thenReturn(List.of(item));

        mvc.perform(get("/items/search?text=name")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
            .andExpect(jsonPath("$.[0].name", is(item.getName())))
            .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
            .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));
    }

    @Test
    @SneakyThrows
    void createComment() {
        User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();
        IncomingCommentDto commentDto = IncomingCommentDto.builder()
            .item(item.getId())
            .user(user.getId())
            .text("text")
            .build();
        Comment comment = Comment.builder()
            .id(1L)
            .text("text")
            .item(item)
            .user(user)
            .authorName(user.getName())
            .build();
        when(itemService.createComment(any(Comment.class), anyLong(), anyLong()))
            .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                .content(objectMapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
            .andExpect(jsonPath("$.text", is(comment.getText())))
            .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
            .andExpect(jsonPath("$.user.id", is(comment.getUser().getId()), Long.class));
    }
}