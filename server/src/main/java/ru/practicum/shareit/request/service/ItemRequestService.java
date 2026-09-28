package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestOnItemDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, NewRequestOnItemDto request);

    Collection<ItemRequestDto> getUserRequests(Long userId);

    Collection<ItemRequestDto> getAllOtherUsersRequests(Long userId);

    ItemRequestDto getRequestById(Long requestId);
}
