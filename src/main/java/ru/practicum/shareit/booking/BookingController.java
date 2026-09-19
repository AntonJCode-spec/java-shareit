package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public Collection<BookingDto> findAllBookingByBookerId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                           @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findOwnerBookings(ownerId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @PostMapping
    public BookingDto createNewBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                       @Valid @RequestBody NewBookingRequest newBookingRequest) {
        return bookingService.createBooking(bookerId, newBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto reviewBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.reviewBooking(ownerId, bookingId, approved);
    }


}
