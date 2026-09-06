package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    Optional<Item> findItemById(Long itemId);

    Collection<Item> findItemsByOwnerId(Long ownerId);

    Collection<Item> searchSuitableItems(String text);
}
