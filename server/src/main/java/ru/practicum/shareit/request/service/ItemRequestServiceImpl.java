package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.OutgoingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User getUser(Long creatorId) {
        return userRepository.findById(creatorId).orElseThrow(
            () -> new NotFoundException(String.format("User %d not found", creatorId))
        );
    }

    private List<Item> getResponsesForRequest(ItemRequest itemRequest) {
        System.out.println(itemRepository.findAllByRequest(itemRequest));
        return itemRepository.findAllByRequest(itemRequest);
    }

    private List<Item> getResponsesForRequestList(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIn(requests);
    }

    @Override
    public OutgoingItemRequestDto createItemRequest(ItemRequest itemRequest, Long creatorId) {
        User creator = getUser(creatorId);
        itemRequest.setCreator(creator);
        log.info("Requests of user {} created.", creatorId);
        return toOutgoingDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutgoingItemRequestDto> getUserRequests(Long userId) {
        User creator = getUser(userId);

        Sort sortByCreationTime = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = requestRepository.findAllByCreator(creator, sortByCreationTime);
        List<Item> responses = getResponsesForRequestList(requests);
        System.out.println(responses);
        List<OutgoingItemDto> responseDtos = ItemDtoMapper.toOutgoingDtoList(responses);
        List<OutgoingItemRequestDto> dtos = toOutgoingDtoList(requests);

        for (OutgoingItemRequestDto request : dtos) {
            request.setItems(responseDtos.stream()
                .filter(itemResponse -> itemResponse.getRequestId().equals(request.getId()))
                .collect(Collectors.toList()));
        }
        log.info("Requests of user {} received.", userId);
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutgoingItemRequestDto> getPaginatedRequests(Long userId, Integer from, Integer size) {
        Sort sortByCreationTime = Sort.by(Sort.Direction.DESC, "created");

        Pageable page = PageRequest.of(from, size, sortByCreationTime);

        List<ItemRequest> requests = requestRepository.findAllByCreatorIdNot(userId, page);
        List<Item> responses = getResponsesForRequestList(requests);
        List<OutgoingItemDto> responseDtos = ItemDtoMapper.toOutgoingDtoList(responses);
        List<OutgoingItemRequestDto> dtos = toOutgoingDtoList(requests);

        dtos.forEach(dto -> dto.setItems(
            responseDtos.stream()
                .filter(itemResponse -> itemResponse.getRequestId().equals(dto.getId()))
                .collect(Collectors.toList()))
        );
        log.info("Requests of user {} received with pagination.", userId);
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public OutgoingItemRequestDto getItemRequest(Long requestId, Long userId) {
        getUser(userId);
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(
            () -> new NotFoundException(String.format("ItemRequest %d not found.", requestId))
        );
        List<Item> responses = getResponsesForRequest(request);
        List<OutgoingItemDto> responseDtos = ItemDtoMapper.toOutgoingDtoList(responses);
        OutgoingItemRequestDto dto = toOutgoingDto(request);
        dto.setItems(responseDtos);
        log.info("Request {} received.", requestId);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutgoingItemRequestDto> getAll(Long userId) {
        Sort sortByCreationTime = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = requestRepository.findAll(sortByCreationTime);
        List<Item> responses = getResponsesForRequestList(requests);
        List<OutgoingItemDto> responseDtos = ItemDtoMapper.toOutgoingDtoList(responses);

        List<OutgoingItemRequestDto> dtos = toOutgoingDtoList(requests);

        dtos.forEach(dto -> dto.setItems(
            responseDtos.stream()
                .filter(itemResponse -> itemResponse.getRequestId().equals(dto.getId()))
                .collect(Collectors.toList()))
        );
        log.info("All requests received.");
        return dtos;
    }
}