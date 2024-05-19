package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NonUniqueEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniqueEmailException(String.format("Email %s is not unique.", user.getEmail()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        try {
            return repository.findById(userId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NotFoundException(String.format("User %d is not found", userId));
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
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return repository.findAll();
    }
}