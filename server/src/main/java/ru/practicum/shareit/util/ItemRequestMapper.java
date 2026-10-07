package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestOnItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(NewRequestOnItemDto newRequestOnItemDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(newRequestOnItemDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor().getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        if (itemRequest.getItems() == null) {
            itemRequestDto.setItems(null);
        } else {
            itemRequestDto.setItems(itemRequest.getItems().stream()
                    .map(ItemMapper::mapToAnswerRequest)
                    .toList());
        }
        return itemRequestDto;
    }
}
