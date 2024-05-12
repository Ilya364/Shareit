package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;

    private ItemRequest itemRequest;

    @Test
    void createItemRequestTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .creator(user)
            .created(LocalDateTime.now())
            .description("desc")
            .build();
        when(itemRequestRepository.save(itemRequest))
            .thenReturn(itemRequest);
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));

        OutgoingItemRequestDto expected = ItemRequestDtoMapper.toOutgoingDto(itemRequest);

        OutgoingItemRequestDto actual = itemRequestService.createItemRequest(itemRequest, user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void createItemRequestUserNotFoundTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .creator(user)
            .created(LocalDateTime.now())
            .description("desc")
            .build();
        when(itemRequestRepository.save(itemRequest))
            .thenReturn(itemRequest);
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(
            NotFoundException.class,
            () -> itemRequestService.createItemRequest(itemRequest, user.getId())
        );
        assertEquals("User 1 not found", e.getMessage());
    }

    @Test
    void getByUserTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .creator(user)
            .description("desc")
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByCreator(any(User.class), any(Sort.class)))
            .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIn(List.of(itemRequest)))
            .thenReturn(List.of(
                Item.builder()
                    .owner(user)
                    .available(true)
                    .name("name")
                    .request(itemRequest)
                    .owner(user)
                    .build()
            ));
        List<OutgoingItemRequestDto> expected = List.of(ItemRequestDtoMapper.toOutgoingDto(itemRequest));
        expected.get(0).setItems(
            ItemDtoMapper.toOutgoingDtoList(List.of(Item.builder()
                .owner(user)
                .available(true)
                .name("name")
                .request(itemRequest)
                .owner(user)
                .build())
            ));

        List<OutgoingItemRequestDto> actual = itemRequestService.getUserRequests(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getByUserNotFoundTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .creator(user)
            .description("desc")
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(user.getId()));
    }

    @Test
    void getAllWithPaginationTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .creator(user)
            .description("desc")
            .build();
        when(itemRepository.findAllByRequestIn(List.of(itemRequest)))
            .thenReturn(List.of(
                Item.builder()
                    .owner(user)
                    .available(true)
                    .name("name")
                    .request(itemRequest)
                    .owner(user)
                    .build()
            ));
        when(itemRequestRepository.findAllByCreatorIdNot(anyLong(), any(Pageable.class)))
            .thenReturn(List.of(itemRequest));
        List<OutgoingItemRequestDto> expected = List.of(ItemRequestDtoMapper.toOutgoingDto(itemRequest));
        expected.get(0).setItems(
            ItemDtoMapper.toOutgoingDtoList(List.of(Item.builder()
                .owner(user)
                .available(true)
                .name("name")
                .request(itemRequest)
                .owner(user)
                .build())
            ));

        List<OutgoingItemRequestDto> actual = itemRequestService.getPaginatedRequests(user.getId(), 0, 1);

        assertIterableEquals(expected, actual);
    }

    @Test
    void getItemByIdTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .creator(user)
            .description("desc")
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.of(itemRequest));
        OutgoingItemRequestDto expected = ItemRequestDtoMapper.toOutgoingDto(itemRequest);
        expected.setItems(List.of());

        OutgoingItemRequestDto actual = itemRequestService.getItemRequest(1L, 1L);

        assertEquals(expected, actual);
    }

    @Test
    void getItemByIdNotFoundTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .creator(user)
            .description("desc")
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(1L, 1L));
    }

    @Test
    void getAllTest() {
        user = User.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();
        itemRequest = ItemRequest.builder()
            .id(1L)
            .creator(user)
            .description("desc")
            .build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestIn(List.of(itemRequest)))
            .thenReturn(
                List.of(
                    Item.builder()
                        .owner(user)
                        .available(true)
                        .name("name")
                        .request(itemRequest)
                        .owner(user)
                        .build()
                )
            );
        when(itemRequestRepository.findAll(any(Sort.class)))
            .thenReturn(List.of(itemRequest));

        List<OutgoingItemRequestDto> expected = ItemRequestDtoMapper.toOutgoingDtoList(List.of(itemRequest));
        expected.get(0).setItems(
            ItemDtoMapper.toOutgoingDtoList(
                List.of(
                    Item.builder()
                        .owner(user)
                        .available(true)
                        .name("name")
                        .request(itemRequest)
                        .owner(user)
                        .build()
                )
            )
        );

        List<OutgoingItemRequestDto> actual = itemRequestService.getAll(1L);

        assertIterableEquals(expected, actual);
    }
}