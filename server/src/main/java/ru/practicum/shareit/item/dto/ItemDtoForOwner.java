package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDateDto;

@Data
@Getter
@Setter
public class ItemDtoForOwner {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private BookingDateDto lastBooking;
    private BookingDateDto nextBooking;
    private Long requestId;
}
