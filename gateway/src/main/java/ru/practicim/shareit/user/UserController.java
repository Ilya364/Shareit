package ru.practicim.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicim.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto dto) {
        log.info("Request to create user.");
        return client.create(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
        @RequestBody UserDto dto,
        @PathVariable Long userId
    ) {
        log.info("Request to update user {}.", userId);
        return client.update(userId, dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Request to receive user {}.", userId);
        return client.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Request to delete user {}", userId);
        client.deleteById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Request to get all users.");
        return client.getAll();
    }
}
