package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService requestService;
    @Autowired
    private MockMvc mockMvc;
    private ItemRequest request;
    private IncomingItemRequestDto incomingItemRequestDto;

    @BeforeEach
    void setUp() {
        incomingItemRequestDto = IncomingItemRequestDto.builder()
            .description("need item")
            .build();
        User user1 = User.builder()
            .id(1L)
            .name("username")
            .email("mail@mail.ru")
            .build();
        User user2 = User.builder()
            .id(2L)
            .name("user2name")
            .email("mail2@mail.ru")
            .build();
        Item item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("desc")
            .available(true)
            .request(request)
            .owner(user1)
            .build();
        request = ItemRequest.builder()
            .id(1L)
            .creator(user2)
            .description("need item")
            .build();
    }

    @SneakyThrows
    @Test
    void createTest() {
        when(requestService.createItemRequest(any(ItemRequest.class), anyLong()))
            .thenReturn(ItemRequestDtoMapper.toOutgoingDto(request));
        mockMvc.perform(post("/requests")
                .content(objectMapper.writeValueAsString(incomingItemRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
            .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @SneakyThrows
    @Test
    void getUserRequestsTest() {
        when(requestService.getUserRequests(anyLong()))
            .thenReturn(ItemRequestDtoMapper.toOutgoingDtoList(List.of(request)));
        mockMvc.perform(get("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
            .andExpect(jsonPath("$.[0].description", is(request.getDescription())));
    }

    @SneakyThrows
    @Test
    void getAllTest() {
        when(requestService.getAll(anyLong()))
            .thenReturn(ItemRequestDtoMapper.toOutgoingDtoList(List.of(request)));
        mockMvc.perform(get("/requests/all")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
            .andExpect(jsonPath("$.[0].description", is(request.getDescription())));
    }

    @SneakyThrows
    @Test
    void getByIdTest() {
        when(requestService.getItemRequest(anyLong(), anyLong()))
            .thenReturn(ItemRequestDtoMapper.toOutgoingDto(request));
        mockMvc.perform(get("/requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
            .andExpect(jsonPath("$.description", is(request.getDescription())));
    }
}