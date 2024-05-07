package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User user = User.builder()
        .id(1L)
        .name("name")
        .email("ilya@mail.ru")
        .build();
    private Item item = Item.builder()
        .id(1L)
        .name("name")
        .description("description")
        .owner(user)
        .available(true)
        .build();
    private Comment comment = Comment.builder()
        .id(1L)
        .text("text")
        .created(LocalDateTime.now())
        .item(item)
        .user(user)
        .authorName(user.getName())
        .build();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment);
    }

    @Test
    void getAllByItem() {
        Comment received = commentRepository.findAllByItem(item).get(0);

        assertEquals(comment.getId(), received.getId());
        assertEquals(comment.getItem(), received.getItem());
        assertEquals(comment.getText(), received.getText());
        assertEquals(comment.getAuthorName(), received.getAuthorName());
        assertEquals(comment.getUser(), received.getUser());
    }
}