package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.comment.dto.OutgoingCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class CommentDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@yandex.ru")
            .build();
        Item item = Item.builder()
            .id(1L)
            .description("description")
            .name("itemname")
            .build();
        Comment comment = Comment.builder()
            .id(1L)
            .text("text")
            .created(LocalDateTime.now())
            .user(user)
            .item(item)
            .authorName(user.getName())
            .build();
        OutgoingCommentDto expected = OutgoingCommentDto.builder()
            .id(1L)
            .text(comment.getText())
            .created(comment.getCreated())
            .item(item)
            .user(user)
            .authorName(user.getName())
            .build();

        OutgoingCommentDto actual = CommentDtoMapper.toOutgoingDto(comment);

        assertEquals(expected, actual);
    }

    @Test
    void toCommentTest() {
        IncomingCommentDto commentDto = IncomingCommentDto.builder()
            .id(1L)
            .text("text")
            .created(LocalDateTime.now())
            .build();
        Comment expected = Comment.builder()
            .id(1L)
            .text(commentDto.getText())
            .created(commentDto.getCreated())
            .build();

        Comment actual = CommentDtoMapper.toComment(commentDto);

        assertEquals(expected, actual);
    }

    @Test
    void toOutgoingDtoListTest() {
        User user1 = User.builder()
            .id(1L)
            .name("user1name")
            .email("email1@yandex.ru")
            .build();
        Item item1 = Item.builder()
            .id(1L)
            .description("description1")
            .name("itemname1")
            .build();
        Comment comment1 = Comment.builder()
            .id(1L)
            .text("text1")
            .created(LocalDateTime.now())
            .user(user1)
            .item(item1)
            .authorName(user1.getName())
            .build();
        OutgoingCommentDto expected1 = OutgoingCommentDto.builder()
            .id(1L)
            .text(comment1.getText())
            .created(comment1.getCreated())
            .item(item1)
            .user(user1)
            .authorName(user1.getName())
            .build();
        User user2 = User.builder()
            .id(1L)
            .name("user2name")
            .email("email2@yandex.ru")
            .build();
        Item item2 = Item.builder()
            .id(1L)
            .description("description2")
            .name("itemname2")
            .build();
        Comment comment2 = Comment.builder()
            .id(1L)
            .text("text2")
            .created(LocalDateTime.now())
            .user(user2)
            .item(item2)
            .authorName(user2.getName())
            .build();
        OutgoingCommentDto expected2 = OutgoingCommentDto.builder()
            .id(1L)
            .text(comment2.getText())
            .created(comment2.getCreated())
            .item(item2)
            .user(user2)
            .authorName(user2.getName())
            .build();
        List<Comment> comments = List.of(comment1, comment2);
        List<OutgoingCommentDto> expectedList = List.of(expected1, expected2);

        List<OutgoingCommentDto> actualList = CommentDtoMapper.toOutgoingDtoList(comments);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void toCommentListTest() {
        IncomingCommentDto commentDto1 = IncomingCommentDto.builder()
            .id(1L)
            .text("text1")
            .created(LocalDateTime.now())
            .build();
        Comment expected1 = Comment.builder()
            .id(1L)
            .text(commentDto1.getText())
            .created(commentDto1.getCreated())
            .build();
        IncomingCommentDto commentDto2 = IncomingCommentDto.builder()
            .id(2L)
            .text("text2")
            .created(LocalDateTime.now())
            .build();
        Comment expected2 = Comment.builder()
            .id(2L)
            .text(commentDto2.getText())
            .created(commentDto2.getCreated())
            .build();
        List<IncomingCommentDto> dtos = List.of(commentDto1, commentDto2);
        List<Comment> expectedList = List.of(expected1, expected2);

        List<Comment> actualList = CommentDtoMapper.toCommentList(dtos);

        assertIterableEquals(expectedList, actualList);
    }
}