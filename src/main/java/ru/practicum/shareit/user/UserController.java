package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.List;
import static ru.practicum.shareit.user.dto.UserDtoMapper.*;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = service.createUser(mapToUser(userDto));
        return mapToUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @RequestBody UserDto userDto,
            @PathVariable Long userId
    ) {
        User user = service.updateUser(mapToUser(userDto), userId);
        return mapToUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return mapToUserDto(service.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        service.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return mapToUserDtoList(service.getAllUsers());
    }
}