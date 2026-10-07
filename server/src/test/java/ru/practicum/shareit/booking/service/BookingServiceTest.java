package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NonAuthorizedUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void shouldGetBookingById() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        BookingDto booking = createBooking(booker, item);

        BookingDto found = bookingService.getBookingById(booker.getId(), booking.getId());

        assertThat(found.getId()).isEqualTo(booking.getId());
        assertThat(found.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(found.getItem().getId()).isEqualTo(item.getId());
        assertThat(found.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void shouldGetBookingByIdByOwner() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        BookingDto booking = createBooking(booker, item);

        BookingDto found = bookingService.getBookingById(owner.getId(), booking.getId());

        assertThat(found.getId()).isEqualTo(booking.getId());
    }

    @Test
    void shouldThrowWhenGetBookingByIdNotAuthorized() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        UserDto stranger = userService.createUser(createUserRequest("Jane", "jane@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        BookingDto booking = createBooking(booker, item);

        assertThatThrownBy(() -> bookingService.getBookingById(stranger.getId(), booking.getId()))
                .isInstanceOf(NonAuthorizedUserException.class);
    }

    @Test
    void shouldThrowWhenGetBookingByIdNotFound() {
        UserDto user = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));

        assertThatThrownBy(() -> bookingService.getBookingById(user.getId(), 999L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void shouldFindUserBookings() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        createBooking(booker, item);

        Collection<BookingDto> bookings = bookingService.findUserBookings(booker.getId(), BookingState.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void shouldFindOwnerBookings() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        createBooking(booker, item);

        Collection<BookingDto> bookings = bookingService.findOwnerBookings(owner.getId(), BookingState.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void shouldThrowWhenCreateBookingWithInvalidDates() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(2));
        request.setEnd(LocalDateTime.now().plusDays(1)); // end раньше start
        request.setItemId(item.getId());

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), request))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    void shouldThrowWhenCreateBookingForUnavailableItem() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", false));

        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        request.setItemId(item.getId());

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), request))
                .isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void shouldThrowWhenReviewBookingNotOwner() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        UserDto stranger = userService.createUser(createUserRequest("Jane", "jane@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        BookingDto booking = createBooking(booker, item);

        assertThatThrownBy(() -> bookingService.reviewBooking(stranger.getId(), booking.getId(), true))
                .isInstanceOf(NonAuthorizedUserException.class);
    }

    @Test
    void shouldThrowWhenReviewBookingAlreadyReviewed() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", true));

        BookingDto booking = createBooking(booker, item);
        bookingService.reviewBooking(owner.getId(), booking.getId(), true);

        assertThatThrownBy(() -> bookingService.reviewBooking(owner.getId(), booking.getId(), false))
                .isInstanceOf(DataValidationException.class);
    }



    private BookingDto createBooking(UserDto booker, ItemDto item) {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        request.setItemId(item.getId());
        return bookingService.createBooking(booker.getId(), request);
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