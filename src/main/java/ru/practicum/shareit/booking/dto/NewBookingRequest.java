package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {

    @NotNull(message = "Необходимо указывать дату начала брони")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указывать дату окончания брони")
    private LocalDateTime end;

    @NotNull(message = "Необходимо указать id вещи для брони")
    private Long itemId;

}
