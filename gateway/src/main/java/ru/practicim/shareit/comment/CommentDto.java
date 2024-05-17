package ru.practicim.shareit.comment;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String text;
    private Long user;
    private Long item;
    private LocalDateTime created;
}
