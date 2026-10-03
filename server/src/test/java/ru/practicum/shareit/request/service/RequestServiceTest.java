package ru.practicum.shareit.request.service;


import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestOnItemDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RequestServiceTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createRequestTest() {
        UserDto requestor = userService.createUser(createNewUserRequest("user", "user@mail.ru"));
        ItemRequestDto itemRequestDto = requestService.createRequest(requestor.getId(), createNewRequest());

        assertThat(itemRequestDto.getId()).isNotNull();
        assertThat(itemRequestDto.getDescription()).isEqualTo("need item");
        assertThat(itemRequestDto.getRequestor()).isEqualTo(requestor.getId());
        assertThat(itemRequestDto.getCreated()).isNotNull();
        assertThat(itemRequestDto.getItems()).isNull();
    }

    @Test
    void getRequestByIdTest() {
        UserDto requestor = userService.createUser(createNewUserRequest("requestor", "requestor@mail.ru"));
        ItemRequestDto itemRequestDto = requestService.createRequest(requestor.getId(), createNewRequest());

        UserDto owner = userService.createUser(createNewUserRequest("owner", "owner@mail.ru"));
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("name");
        newItemRequest.setDescription("item");
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(itemRequestDto.getId());

        ItemDto itemForRequest = itemService.createItem(owner.getId(), newItemRequest);

        entityManager.flush();
        entityManager.clear();


        itemRequestDto = requestService.getRequestById(itemRequestDto.getId());

        assertThat(itemRequestDto.getItems()).isNotNull();
        assertThat(itemRequestDto.getItems().size()).isEqualTo(1);

        ItemToRequest item = itemRequestDto.getItems().iterator().next();
        assertThat(item.getId()).isEqualTo(itemForRequest.getId());
        assertThat(item.getName()).isEqualTo(itemForRequest.getName());
        assertThat(item.getOwner()).isEqualTo(itemForRequest.getOwner());
    }

    private NewUserRequest createNewUserRequest(String name, String email) {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName(name);
        newUserRequest.setEmail(email);

        return newUserRequest;
    }

    private NewRequestOnItemDto createNewRequest() {
        NewRequestOnItemDto request = new NewRequestOnItemDto();
        request.setDescription("need item");

        return request;
    }
}
