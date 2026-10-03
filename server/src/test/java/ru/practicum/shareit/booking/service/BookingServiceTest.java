package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void shouldCreateBookingAndReviewTest() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));

        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        NewBookingRequest bookingRequest = new NewBookingRequest();
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        bookingRequest.setItemId(item.getId());

        BookingDto booking = bookingService.createBooking(booker.getId(), bookingRequest);

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);

        Booking saved = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(saved.getBooker().getId()).isEqualTo(booker.getId());

        booking = bookingService.reviewBooking(owner.getId(), booking.getId(), true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);

        saved = bookingRepository.findById(booking.getId()).orElseThrow();

        assertThat(saved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }



    private NewUserRequest createUserRequest(String name, String email) {
        NewUserRequest request = new NewUserRequest();
        request.setName(name);
        request.setEmail(email);
        return request;
    }

    private NewItemRequest createItemRequest(String name, boolean available) {
        NewItemRequest request = new NewItemRequest();
        request.setName(name);
        request.setDescription("Описание");
        request.setAvailable(available);
        return request;
    }
}