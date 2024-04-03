package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IsNotOwnerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public Item createItem(Item item, Long owner) {
        if (userRepository.getUserById(owner) != null) {
            return repository.createItem(item, owner);
        }
        throw new NotFoundException("User is not found.");
    }

    @Override
    public Item getItemById(Long id) {
        return repository.getItemById(id);
    }

    @Override
    public Item updateItem(IncomingItemDto incomingItemDto, Long id, Long userId) {
        Item item = getItemById(id);
        if (!Objects.equals(item.getOwner(), userId)) {
            throw new IsNotOwnerException("User " + id + " is not owner of Item " + item.getId() + ".");
        }
        return repository.updateItem(incomingItemDto, id);
    }

    @Override
    public void deleteItemById(Long id) {
        repository.deleteItemById(id);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return repository.getUserItems(userId);
    }

    @Override
    public List<Item> search(String text) {
        return repository.search(text);
    }
}