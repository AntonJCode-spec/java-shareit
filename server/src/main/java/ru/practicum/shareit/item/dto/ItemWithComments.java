package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDateDto;

import java.util.List;

@Data
public class ItemWithComments {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private BookingDateDto lastBooking;
    private BookingDateDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
