package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
public class IncomingCommentDto {
    private Long id;
    private String text;
    private Long user;
    private Long item;
    @CreationTimestamp
    private LocalDateTime created;
}