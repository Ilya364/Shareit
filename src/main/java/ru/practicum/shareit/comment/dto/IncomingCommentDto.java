package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomingCommentDto {
    private Long id;
    @NotEmpty
    private String text;
    private Long user;
    private Long item;
    private LocalDateTime created;
}