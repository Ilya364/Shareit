package ru.practicum.shareit.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.model.Comment;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentDtoMapper {
    public OutgoingCommentDto toOutgoingDto(Comment comment) {
        return OutgoingCommentDto.builder()
            .id(comment.getId())
            .text(comment.getText())
            .user(comment.getUser())
            .item(comment.getItem())
            .authorName(comment.getAuthorName())
            .created(comment.getCreated())
            .build();
    }

    public Comment toComment(IncomingCommentDto dto) {
        return Comment.builder()
            .id(dto.getId())
            .text(dto.getText())
            .created(dto.getCreated())
            .build();
    }

    public List<OutgoingCommentDto> toOutgoingDtoList(List<Comment> comments) {
        return comments.stream()
            .map(CommentDtoMapper::toOutgoingDto)
            .collect(Collectors.toList());
    }

    public List<Comment> toCommentList(List<IncomingCommentDto> dtos) {
        return dtos.stream()
            .map(CommentDtoMapper::toComment)
            .collect(Collectors.toList());
    }
}