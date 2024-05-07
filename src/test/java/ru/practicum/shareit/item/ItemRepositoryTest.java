package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private static User user1;
    private static User user2;

    private static ItemRequest itemRequest;
    private static Item item;

    @BeforeAll
    static void setUp() {
        user2 = User.builder()
            .id(2L)
            .name("username2")
            .email("user2@mail.ru")
            .build();
        user1 = User.builder()
            .id(1L)
            .name("username1")
            .email("user1@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .description("for item")
            .creator(user1)
            .created(LocalDateTime.now())
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .owner(user1)
            .available(true)
            .description("description")
            .request(itemRequest)
            .build();
    }

    @BeforeEach
    void save() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByItemOwnerTest() {
        Item received = itemRepository.findAllByOwnerId(
            1L, Sort.by(Sort.Direction.ASC, "id")
        ).get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
        assertEquals(item.getOwner(), received.getOwner());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByTextInNameTest() {
        Item received = itemRepository.findAllByNameContainingIgnoreCase("item").get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
        assertEquals(item.getOwner(), received.getOwner());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByTextInDescriptionTest() {
        Item received = itemRepository.findAllByDescriptionContainingIgnoreCase("desc").get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
        assertEquals(item.getOwner(), received.getOwner());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByRequestTest() {
        Item received = itemRepository.findAllByRequest(itemRequest).get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
        assertEquals(item.getOwner(), received.getOwner());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByRequestListTest() {
        Item received = itemRepository.findAllByRequestIn(List.of(itemRequest)).get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
        assertEquals(item.getOwner(), received.getOwner());
    }
}