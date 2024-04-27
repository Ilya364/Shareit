package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NonUniqueEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        try {
            return repository.save(user);
        } catch (ConstraintViolationException e) {
            throw new NonUniqueEmailException("Email " + user.getEmail() + " is not unique.");
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            return repository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User " + id + " not found.");
        }
    }

    @Override
    public User updateUser(User user) {
        return repository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }
}