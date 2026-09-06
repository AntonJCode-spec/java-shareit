package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getItemsByOwnerId(Long ownerId);

    ItemDto getItemById(Long id);

    Collection<ItemDto> searchSuitableItems(String text);

    ItemDto createItem(Long userId, NewItemRequest newItemRequest);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest);
}
