package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner().getId());

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        } else {
            itemDto.setRequestId(null);
        }
        return itemDto;
    }

    public static Item mapToItem(NewItemRequest newItemRequest) {
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setAvailable(newItemRequest.getAvailable());
        return item;
    }

    public static void updateField(Item updatedItem, UpdateItemRequest updateItemRequest) {
        if (updateItemRequest.getName() != null && !updateItemRequest.getName().isBlank()) {
            updatedItem.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.getDescription() != null && !updateItemRequest.getDescription().isBlank()) {
            updatedItem.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.getAvailable() != null) {
            updatedItem.setAvailable(updateItemRequest.getAvailable());
        }
    }
}
