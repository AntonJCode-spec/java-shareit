package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NonAuthorizedUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemStorage.findItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        return itemStorage.findItemById(id)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с таким id не существует"));
    }

    @Override
    public Collection<ItemDto> searchSuitableItems(String text) {

        return itemStorage.searchSuitableItems(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Long userId, NewItemRequest newItemRequest) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с переданным id не существует"));

        Item itemToAdd = ItemMapper.mapToItem(newItemRequest);
        itemToAdd.setOwner(owner);

        return ItemMapper.mapToItemDto(itemStorage.createItem(itemToAdd));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item itemToUpdate = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещи с переданным id не существует"));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new NonAuthorizedUserException("Обновлять данные о вещи может только её владелец");
        }

        ItemMapper.updateField(itemToUpdate, updateItemRequest);

        itemStorage.updateItem(itemToUpdate);


        return ItemMapper.mapToItemDto(itemToUpdate);
    }
}
