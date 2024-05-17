package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.IncomingUserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    private IncomingUserDto dto;
    private User user;

    @BeforeEach
    void setUp() {
        dto = IncomingUserDto.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        user = UserDtoMapper.toUser(dto);
    }

    @Test
    @SneakyThrows
    void createUserTest() {
        when(userService.createUser(user))
            .thenReturn(user);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @SneakyThrows
    void getUserTest() {
        when(userService.getUserById(anyLong()))
            .thenReturn(user);

        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @SneakyThrows
    void updateUserTest() {
        when(userService.updateUser(user))
            .thenReturn(user);
        when(userService.getUserById(anyLong()))
            .thenReturn(user);

        mockMvc.perform(patch("/users/1")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @SneakyThrows
    void deleteUserByIdTest() {
        mockMvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService, Mockito.times(1))
            .deleteUserById(1L);
    }

    @Test
    @SneakyThrows
    void getAllTest() {
        when(userService.getAllUsers())
            .thenReturn(List.of(user));

        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id", is(user.getId()), Long.class))
            .andExpect(jsonPath("$.[0].name", is(user.getName())))
            .andExpect(jsonPath("$.[0].email", is(user.getEmail())));
    }
}