package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long owner);

    OutgoingItemDto getItemWithCommentsAndBookingsById(Long id, Long user);

    Item getItem(Long id);

    Item updateItem(Item item, Long userId);

    void deleteItemById(Long id);

    List<OutgoingItemDto> getUserItems(Long userId);

    List<Item> search(String text);

    Comment createComment(Comment comment, Long itemId, Long userId);

    List<Comment> getItemComments(Long itemId);
}