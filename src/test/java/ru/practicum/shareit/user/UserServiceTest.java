package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NonUniqueEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserServiceImpl userService;
    private final User user = User.builder()
        .name("name")
        .email("email@mail.ru")
        .build();

    @Test
    void createUserTest() {
        User result = userService.createUser(user);

        assertEquals(user, result);
    }

    @Test
    void createUserNonUniqueEmailTest() {
        userService.createUser(user);
        User sameEmailUser = User.builder()
            .name("name5")
            .email(user.getEmail())
            .build();

        NonUniqueEmailException e =
            assertThrows(NonUniqueEmailException.class, () -> userService.createUser(sameEmailUser));
        assertEquals("Email email@mail.ru is not unique.", e.getMessage());
    }

    @Test
    void getUserTest() {
        userService.createUser(user);

        User actual = userService.getUserById(user.getId());

        assertEquals(user, actual);
    }

    @Test
    void getUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUserTest() {
        userService.createUser(user);

        User forUpdate = User.builder()
            .id(user.getId())
            .name("updated")
            .email(user.getEmail())
            .build();
        userService.updateUser(forUpdate);

        assertEquals(forUpdate, userService.getUserById(user.getId()));
    }

    @Test
    void deleteByIdTest() {
        userService.createUser(user);

        userService.deleteUserById(user.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }


    @Test
    void getAllTest() {
        userService.createUser(user);
        List<User> expected = List.of(user);

        List<User> actual = userService.getAllUsers();

        assertIterableEquals(expected, actual);
    }
}