package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NonUniqueEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final User user = User.builder()
        .id(1L)
        .name("name")
        .email("email@mail.ru")
        .build();

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class)))
            .thenReturn(user);
        User result = userService.createUser(user);

        assertEquals(user, result);
    }

    @Test
    void createUserNonUniqueEmailTest() {
        when(userRepository.save(any(User.class)))
            .thenThrow(ConstraintViolationException.class);

        NonUniqueEmailException e = assertThrows(NonUniqueEmailException.class, () -> userService.createUser(user));
        assertEquals("Email email@mail.ru is not unique.", e.getMessage());
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));

        User actual = userService.getUserById(1L);

        assertEquals(user, actual);
    }

    @Test
    void getUserNotFoundTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUserTest() {
        when(userRepository.save(any(User.class)))
            .thenReturn(user);

        User expected = userService.updateUser(user);

        assertEquals(expected, user);
    }

    @Test
    void deleteByIdTest() {
        userService.deleteUserById(1L);

        verify(userRepository, Mockito.times(1))
            .deleteById(1L);
    }


    @Test
    void getAllTest() {
        when(userRepository.findAll())
            .thenReturn(List.of(user));
        List<User> expected = List.of(user);

        List<User> actual = userService.getAllUsers();

        assertIterableEquals(expected, actual);
    }
}