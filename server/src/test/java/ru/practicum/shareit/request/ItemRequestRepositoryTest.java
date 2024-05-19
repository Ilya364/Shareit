package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private static User user;
    private static ItemRequest itemRequest;

    @BeforeAll
    static void setUp() {
        user = User.builder()
            .id(1L)
            .name("username1")
            .email("user1@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .description("for item")
            .creator(user)
            .created(LocalDateTime.now())
            .build();
    }

    @BeforeEach
    void save() {
        userRepository.save(user);
        requestRepository.save(itemRequest);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByCreator() {
        ItemRequest received = requestRepository.findAllByCreator(
            user, Sort.by(Sort.Direction.ASC, "created")
        ).get(0);

        assertEquals(itemRequest.getId(), received.getId());
        assertEquals(itemRequest.getDescription(), received.getDescription());
        assertEquals(itemRequest.getCreator(), received.getCreator());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByCreatorNotIn() {
        ItemRequest received = requestRepository.findAllByCreatorIdNot(
            2L, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id"))
        ).get(0);

        assertEquals(itemRequest.getId(), received.getId());
        assertEquals(itemRequest.getDescription(), received.getDescription());
        assertEquals(itemRequest.getCreator(), received.getCreator());
    }
}