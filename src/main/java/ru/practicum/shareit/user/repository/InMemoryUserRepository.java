package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NonUniqueEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import java.util.*;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;

    private void isEmailUnique(String email, Long id) {
        try {
            users.values().stream()
                    .filter(user -> Objects.equals(user.getEmail(), email) && !Objects.equals(user.getId(), id))
                    .findFirst()
                    .orElseThrow();
            throw new NonUniqueEmailException(String.format("\"%s\" is non-unique email.", email));
        } catch (NoSuchElementException e) {
        }
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email can't be null.");
        }
        isEmailUnique(user.getEmail(), user.getId());
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("User {} is created.", user.getId());
        return users.get(user.getId());
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User " + id + " is not found.");
        }
        log.info("User {} is received.", id);
        return user;
    }

    @Override
    public User updateUser(User user, Long id) {
        User updated = users.get(id);
        if (user.getName() != null) {
            if (!user.getName().isEmpty()) {
                updated.setName(user.getName());
            } else {
                throw new ValidationException("Name can't be empty.");
            }
        }
        if (user.getEmail() != null) {
            isEmailUnique(user.getEmail(), id);
            updated.setEmail(user.getEmail());
        }
        log.info("User {} is updated.", id);
        return getUserById(id);
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
        log.info("User {} is deleted.", id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}