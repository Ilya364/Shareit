package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        return repository.createUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return repository.getUserById(id);
    }

    @Override
    public User updateUser(User user, Long id) {
        return repository.updateUser(user, id);
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }
}