package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemToRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestOnItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        ItemToRequest item = new ItemToRequest();
        item.setId(1L);
        item.setName("Дрель");
        item.setOwner(1L);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель");
        itemRequestDto.setRequestor(1L);
        itemRequestDto.setCreated(LocalDateTime.of(2030, 1, 1, 10, 0, 0));
        itemRequestDto.setItems(List.of(item));
    }

    @Test
    void getRequestsByRequestorIdTest() throws Exception {
        when(itemRequestService.getUserRequests(eq(1L)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[0].requestor").value(1))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].items[0].owner").value(1));

        verify(itemRequestService).getUserRequests(eq(1L));
    }

    @Test
    void getRequestsByRequestorIdWhenUserNotFoundTest() throws Exception {
        when(itemRequestService.getUserRequests(eq(100L)))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.description").value("Пользователь не найден"));

        verify(itemRequestService).getUserRequests(eq(100L));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.getRequestById(eq(1L)))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.requestor").value(1))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Дрель"));

        verify(itemRequestService).getRequestById(eq(1L));
    }

    @Test
    void getRequestByIdWhenNotFoundTest() throws Exception {
        when(itemRequestService.getRequestById(eq(100L)))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mvc.perform(get("/requests/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.description").value("Запрос не найден"));

        verify(itemRequestService).getRequestById(eq(100L));
    }

    @Test
    void getAllOtherUsersRequestsTest() throws Exception {
        when(itemRequestService.getAllOtherUsersRequests(eq(1L)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[0].requestor").value(1));

        verify(itemRequestService).getAllOtherUsersRequests(eq(1L));
    }

    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.createRequest(eq(1L), any()))
                .thenReturn(itemRequestDto);

        NewRequestOnItemDto newRequest = new NewRequestOnItemDto();
        newRequest.setDescription("Нужна дрель");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.requestor").value(1))
                .andExpect(jsonPath("$.created").exists());

        verify(itemRequestService).createRequest(eq(1L), any());
    }

    @Test
    void createRequestWhenUserNotFoundTest() throws Exception {
        when(itemRequestService.createRequest(eq(100L), any()))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));

        NewRequestOnItemDto newRequest = new NewRequestOnItemDto();
        newRequest.setDescription("Нужна дрель");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.description").value("Пользователь не найден"));

        verify(itemRequestService).createRequest(eq(100L), any());
    }
}
