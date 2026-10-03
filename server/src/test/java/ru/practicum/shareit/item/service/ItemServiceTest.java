package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

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
}