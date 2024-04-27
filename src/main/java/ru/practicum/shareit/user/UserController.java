package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.IncomingUserDto;
import ru.practicum.shareit.user.dto.OutgoingUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.List;
import static ru.practicum.shareit.user.dto.UserDtoMapper.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    public OutgoingUserDto createUser(@Valid @RequestBody IncomingUserDto incomingUserDto) {
        log.info("Request to create user.");
        User user = service.createUser(toUser(incomingUserDto));
        return toOutgoingDto(user);
    }

    @PatchMapping("/{userId}")
    public OutgoingUserDto updateUser(
            @RequestBody IncomingUserDto incomingUserDto,
            @PathVariable Long userId
    ) {
        log.info("Request to update user {}.", userId);
        User user = service.getUserById(userId);
        partialUpdateUser(incomingUserDto, user);
        service.updateUser(user);
        return toOutgoingDto(user);
    }

    @GetMapping("/{userId}")
    public OutgoingUserDto getUserById(@PathVariable Long userId) {
        log.info("Request to receive user {}.", userId);
        return toOutgoingDto(service.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Request to delete user {}", userId);
        service.deleteUserById(userId);
    }

    @GetMapping
    public List<OutgoingUserDto> getAllUsers() {
        log.info("Request to get all users.");
        return toOutgoingDtoList(service.getAllUsers());
    }
}