package ru.practicum.shareit.item.service;



import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDtoForOwner> getItemsByOwnerId(Long ownerId);

    ItemWithComments getItemById(Long id, Long itemId);

    Collection<ItemDto> searchSuitableItems(String text);

    ItemDto createItem(Long userId, NewItemRequest newItemRequest);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    CommentDto addNewComment(Long authorId, Long itemId, NewCommentRequest text);
}
