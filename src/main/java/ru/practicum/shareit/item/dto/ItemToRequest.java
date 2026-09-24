package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemToRequest {
    private Long id;
    private String name;
    private Long owner;
}
