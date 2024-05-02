package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Data
@Builder
public class OutgoingCommentDto {
    private Long id;
    private String text;
    private User user;
    private Item item;
    private String authorName;
    private LocalDateTime created;
}