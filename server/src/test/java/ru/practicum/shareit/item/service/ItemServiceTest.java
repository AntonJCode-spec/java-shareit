package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NonAuthorizedUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
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
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void shouldUpdateItemTest() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("Oliver");
        newUser.setEmail("oliver@mail.ru");
        UserDto owner = userService.createUser(newUser);

        NewItemRequest newItem = new NewItemRequest();
        newItem.setName("Старое имя");
        newItem.setDescription("Старое описание");
        newItem.setAvailable(true);
        ItemDto item = itemService.createItem(owner.getId(), newItem);

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Новое имя");
        updateRequest.setDescription("Новое описание");
        updateRequest.setAvailable(false);

        ItemDto updated = itemService.updateItem(owner.getId(), item.getId(), updateRequest);

        assertThat(updated.getId()).isEqualTo(item.getId());
        assertThat(updated.getName()).isEqualTo("Новое имя");
        assertThat(updated.getDescription()).isEqualTo("Новое описание");
        assertThat(updated.getAvailable()).isFalse();

        Item saved = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Новое имя");
        assertThat(saved.getDescription()).isEqualTo("Новое описание");
        assertThat(saved.getAvailable()).isFalse();
    }

    @Test
    void shouldGetUserItems() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("Oliver");
        newUser.setEmail("oliver@mail.ru");
        UserDto owner = userService.createUser(newUser);

        NewItemRequest newItem = new NewItemRequest();
        newItem.setName("Дрель");
        newItem.setDescription("Мощная дрель");
        newItem.setAvailable(true);
        ItemDto createdItem = itemService.createItem(owner.getId(), newItem);

        Collection<ItemDtoForOwner> items = itemService.getItemsByOwnerId(owner.getId());

        assertThat(items).hasSize(1);

        ItemDtoForOwner item = items.iterator().next();
        assertThat(item.getId()).isEqualTo(createdItem.getId());
        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Мощная дрель");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner.getId());

        assertThat(item.getLastBooking()).isNull();
        assertThat(item.getNextBooking()).isNull();
    }

    @Test
    void shouldSearchItems() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));

        itemService.createItem(owner.getId(), createItemRequest("Дрель", "Мощная дрель", true));
        itemService.createItem(owner.getId(), createItemRequest("Молоток", "Тяжёлый молоток", true));
        itemService.createItem(owner.getId(), createItemRequest("Книга", "Интересная книга", false));

        Collection<ItemDto> found = itemService.searchSuitableItems("дрель");

        assertThat(found).hasSize(1);
        assertThat(found.iterator().next().getName()).isEqualTo("Дрель");
    }

    @Test
    void shouldReturnEmptyWhenSearchTextIsBlank() {
        Collection<ItemDto> found = itemService.searchSuitableItems("");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldSearchOnlyAvailableItems() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));

        itemService.createItem(owner.getId(), createItemRequest("Дрель", "Мощная дрель", true));
        itemService.createItem(owner.getId(), createItemRequest("Дрель", "Старая дрель", false));

        Collection<ItemDto> found = itemService.searchSuitableItems("дрель");

        assertThat(found).hasSize(1);
        assertThat(found.iterator().next().getAvailable()).isTrue();
    }

    @Test
    void shouldGetItemById() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        ItemDto created = itemService.createItem(owner.getId(), createItemRequest("Дрель", "Мощная дрель", true));

        ItemWithComments found = itemService.getItemById(created.getId(), owner.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Дрель");
        assertThat(found.getDescription()).isEqualTo("Мощная дрель");
        assertThat(found.getComments()).isEmpty();
    }

    @Test
    void shouldThrowWhenGetItemByIdNotFound() {
        assertThatThrownBy(() -> itemService.getItemById(999L, 1L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void shouldAddComment() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto booker = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", "Мощная дрель", true));

        NewBookingRequest bookingRequest = new NewBookingRequest();
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        bookingRequest.setItemId(item.getId());
        BookingDto booking = bookingService.createBooking(booker.getId(), bookingRequest);
        bookingService.reviewBooking(owner.getId(), booking.getId(), true);

        Booking savedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        savedBooking.setStart(LocalDateTime.now().minusDays(2));
        savedBooking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(savedBooking);

        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Отличная дрель!");

        CommentDto comment = itemService.addNewComment(booker.getId(), item.getId(), commentRequest);

        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getText()).isEqualTo("Отличная дрель!");
        assertThat(comment.getAuthorName()).isEqualTo("John");
    }

    @Test
    void shouldThrowWhenAddCommentWithoutBooking() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto stranger = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", "Мощная дрель", true));

        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Комментарий");

        assertThatThrownBy(() -> itemService.addNewComment(stranger.getId(), item.getId(), commentRequest))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    void shouldThrowWhenUpdateItemNotOwner() {
        UserDto owner = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto stranger = userService.createUser(createUserRequest("John", "john@mail.ru"));
        ItemDto item = itemService.createItem(owner.getId(), createItemRequest("Дрель", "Мощная дрель", true));

        UpdateItemRequest update = new UpdateItemRequest();
        update.setName("Новое имя");

        assertThatThrownBy(() -> itemService.updateItem(stranger.getId(), item.getId(), update))
                .isInstanceOf(NonAuthorizedUserException.class);
    }

    private NewUserRequest createUserRequest(String name, String email) {
        NewUserRequest request = new NewUserRequest();
        request.setName(name);
        request.setEmail(email);
        return request;
    }

    private NewItemRequest createItemRequest(String name, String description, boolean available) {
        NewItemRequest request = new NewItemRequest();
        request.setName(name);
        request.setDescription(description);
        request.setAvailable(available);
        return request;
    }
}