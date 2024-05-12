package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BookingNoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private User user;

    @Test
    void createItemTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        when(itemRepository.save(item))
            .thenReturn(item);
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));

        Item expected = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();

        assertEquals(expected, itemService.createItem(item, user.getId()));
    }

    @Test
    void createItemUserNotFoundTest() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();

        assertThrows(NotFoundException.class, () -> itemService.createItem(item, 1L));
    }

    @Test
    void getItemByIdTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item));

        OutgoingItemDto expected = ItemDtoMapper.toOutgoingDto(item);
        expected.setComments(List.of());

        OutgoingItemDto actual = itemService.getItemById(item.getId(), user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getItemByIdNotFoundTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(item.getId() + 1, user.getId()));
    }

    @Test
    void getItemTest() {
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .build();
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item));

        Item actual = itemService.getItem(1L);

        assertEquals(item, actual);
    }

    @Test
    void getItemNotFoundTest() {
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .build();
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(1L));
    }

    @Test
    void updateItemTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        Item updated = Item.builder()
            .id(1L)
            .name("updated")
            .description("desc")
            .available(false)
            .owner(user)
            .build();
        when(itemRepository.save(updated))
            .thenReturn(updated);

        assertEquals(updated, itemService.updateItem(updated, user.getId()));
    }

    @Test
    void updateItemNoAccessTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        Item updated = Item.builder()
            .id(1L)
            .name("updated")
            .description("desc")
            .available(false)
            .owner(user)
            .build();
        when(itemRepository.save(updated))
            .thenReturn(updated);

        assertThrows(BookingNoAccessException.class, () -> itemService.updateItem(updated, user.getId() + 1));
    }

    @Test
    void deleteItemByIdTest() {
        itemService.deleteItemById(1L);
        verify(itemRepository, Mockito.times(1))
            .deleteById(1L);
    }

    @Test
    void getAllByOwnerTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        User booker = User.builder()
            .id(2L)
            .name("booker")
            .email("booker@mail.ru")
            .build();
        Booking booking = Booking.builder()
            .id(1L)
            .booker(booker)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2))
            .item(item)
            .status(Status.APPROVED)
            .build();
        when(itemRepository.findAllByOwnerId(anyLong(), any(Sort.class)))
            .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIn(any(), any()))
            .thenReturn(List.of(booking));

        List<OutgoingItemDto> expected = ItemDtoMapper.toOutgoingDtoList(List.of(item));
        expected.get(0).setComments(List.of());
        expected.get(0).setLastBooking(BookingDtoMapper.toOutForItemDto(booking));

        List<OutgoingItemDto> actual = itemService.getUserItems(1L);

        assertIterableEquals(expected, actual);
    }

    @Test
    void searchByText() {
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(user)
            .build();
        when(itemRepository.findAllByNameContainingIgnoreCase(any(String.class)))
            .thenReturn(List.of(item));
        when(itemRepository.findAllByDescriptionContainingIgnoreCase(any(String.class)))
            .thenReturn(List.of(item));
        List<Item> expected = List.of(item);

        List<Item> actual = itemService.search("ite");

        assertIterableEquals(expected, actual);
    }

    @Test
    void createCommentTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(
                User.builder()
                    .id(2L)
                    .name("user2")
                    .email("email2@mail.ru")
                    .build()
            )
            .build();
        Comment comment = Comment.builder()
            .id(1L)
            .text("good")
            .created(LocalDateTime.now())
            .authorName(user.getName())
            .user(user)
            .item(item)
            .build();
        Booking booking = Booking.builder()
            .id(1L)
            .booker(user)
            .start(LocalDateTime.now().minusHours(2))
            .end(LocalDateTime.now())
            .item(item)
            .status(Status.APPROVED)
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker(any(User.class), any(Sort.class)))
            .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item));
        when(commentRepository.save(comment))
            .thenReturn(comment);

        assertEquals(comment, itemService.createComment(comment, item.getId(), comment.getUser().getId()));
    }

    @Test
    void createCommentUserNotFoundTest() {
        user = User.builder()
            .id(1L)
            .name("username")
            .email("ilya@mail.ru")
            .build();
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(
                User.builder()
                    .id(2L)
                    .name("user2")
                    .email("email2@mail.ru")
                    .build()
            )
            .build();
        Comment comment = Comment.builder()
            .id(1L)
            .text("good")
            .created(LocalDateTime.now())
            .authorName(user.getName())
            .user(user)
            .item(item)
            .build();
        Booking booking = Booking.builder()
            .id(1L)
            .booker(user)
            .start(LocalDateTime.now().minusHours(2))
            .end(LocalDateTime.now())
            .item(item)
            .status(Status.APPROVED)
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
        when(bookingRepository.findAllByBooker(any(User.class), any(Sort.class)))
            .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item));
        when(commentRepository.save(comment))
            .thenReturn(comment);

        assertThrows(
            NotFoundException.class, () -> itemService.createComment(comment, item.getId(), comment.getUser().getId())
        );
    }

    @Test
    void getItemCommentsTest() {
        item = Item.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .owner(
                User.builder()
                    .id(2L)
                    .name("user2")
                    .email("email2@mail.ru")
                    .build()
            )
            .build();
        Comment comment = Comment.builder()
            .id(1L)
            .text("good")
            .created(LocalDateTime.now())
            .user(user)
            .item(item)
            .build();
        when(commentRepository.findAllByItem(any(Item.class)))
            .thenReturn(List.of(comment));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.ofNullable(item));
        List<Comment> expected = List.of(comment);

        List<Comment> actual = itemService.getItemComments(1L);

        assertIterableEquals(expected, actual);
    }
}