package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

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

    public static ItemWithComments mapToItemWithComments(
            Item item,
            BookingDateDto lastBooking,
            BookingDateDto nextBooking,
            List<CommentDto> comments) {

        ItemWithComments dto = new ItemWithComments();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
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

    public static ItemDtoForOwner mapToItemDtoForOwner(Item item, Booking lastBooking, Booking nextBooking) {
        ItemDtoForOwner itemDtoForOwner = new ItemDtoForOwner();
        itemDtoForOwner.setId(item.getId());
        itemDtoForOwner.setName(item.getName());
        itemDtoForOwner.setDescription(item.getDescription());
        itemDtoForOwner.setAvailable(item.getAvailable());
        itemDtoForOwner.setOwner(item.getOwner().getId());

        if (lastBooking != null) {
            itemDtoForOwner.setLastBooking(BookingMapper.mapToBookingDate(lastBooking));
        } else {
            itemDtoForOwner.setLastBooking(null);
        }

        if (nextBooking != null) {
            itemDtoForOwner.setNextBooking(BookingMapper.mapToBookingDate(nextBooking));
        } else {
            itemDtoForOwner.setNextBooking(null);
        }

        if (item.getRequest() != null) {
            itemDtoForOwner.setRequestId(item.getRequest().getId());
        } else {
            itemDtoForOwner.setRequestId(null);
        }

        return itemDtoForOwner;
    }
}
