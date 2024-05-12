package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Sort sort);

    List<Item> findAllByNameContainingIgnoreCase(String text);

    List<Item> findAllByDescriptionContainingIgnoreCase(String text);

    List<Item> findAllByRequest(ItemRequest request);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);
}