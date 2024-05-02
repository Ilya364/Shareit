package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.OutForItemBooking;
import ru.practicum.shareit.comment.model.Comment;
import java.util.List;

@Data
@Builder
public class OutgoingItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private OutForItemBooking nextBooking;
    private OutForItemBooking lastBooking;
    private List<Comment> comments;
}