package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;

import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemToBookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static Booking mapToBooking(User booker, Item item, NewBookingRequest newBookingRequest) {
        Booking booking = new Booking();
        booking.setStart(newBookingRequest.getStart());
        booking.setEnd(newBookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);

        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(new ItemToBookingDto(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setBooker(new BookerDto(booking.getBooker().getId()));
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static BookingDateDto mapToBookingDate(Booking booking) {
        BookingDateDto bookingDateDto = new BookingDateDto();
        bookingDateDto.setStartBooking(booking.getStart());
        bookingDateDto.setEndBooking(booking.getEnd());

        return bookingDateDto;
    }
}
