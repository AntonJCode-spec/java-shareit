package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NonAuthorizedUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    private static final String ITEM_NAME = "name";
    private static final String ITEM_DESCRIPTION = "description";

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDtoForOwner itemDtoForOwner;
    private ItemWithComments itemWithComments;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName(ITEM_NAME);
        itemDto.setDescription(ITEM_DESCRIPTION);
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);
        itemDto.setRequestId(null);

        itemDtoForOwner = new ItemDtoForOwner();
        itemDtoForOwner.setId(1L);
        itemDtoForOwner.setName(ITEM_NAME);
        itemDtoForOwner.setDescription(ITEM_DESCRIPTION);
        itemDtoForOwner.setAvailable(true);
        itemDtoForOwner.setOwner(1L);
        itemDtoForOwner.setLastBooking(null);
        itemDtoForOwner.setNextBooking(null);
        itemDtoForOwner.setRequestId(null);

        itemWithComments = new ItemWithComments();
        itemWithComments.setId(1L);
        itemWithComments.setName(ITEM_NAME);
        itemWithComments.setDescription(ITEM_DESCRIPTION);
        itemWithComments.setAvailable(true);
        itemWithComments.setOwner(1L);
        itemWithComments.setLastBooking(null);
        itemWithComments.setNextBooking(null);
        itemWithComments.setComments(List.of());
        itemWithComments.setRequestId(null);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text");
        commentDto.setAuthorName("Oliver");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void getItemsByOwnerIdTest() throws Exception {
        when(itemService.getItemsByOwnerId(eq(1L)))
                .thenReturn(List.of(itemDtoForOwner));

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(ITEM_NAME))
                .andExpect(jsonPath("$[0].description").value(ITEM_DESCRIPTION))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].owner").value(1));

        verify(itemService).getItemsByOwnerId(eq(1L));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(eq(1L), eq(1L)))
                .thenReturn(itemWithComments);

        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(ITEM_NAME))
                .andExpect(jsonPath("$.description").value(ITEM_DESCRIPTION))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner").value(1))
                .andExpect(jsonPath("$.comments").isArray());

        verify(itemService).getItemById(eq(1L), eq(1L));
    }

    @Test
    void getItemByIdWhenNotFoundTest() throws Exception {
        when(itemService.getItemById(eq(100L), eq(1L)))
                .thenThrow(new ItemNotFoundException("Вещь не найдена"));

        mvc.perform(get("/items/100")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.description").value("Вещь не найдена"));

        verify(itemService).getItemById(eq(100L), eq(1L));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchSuitableItems(eq("name")))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(ITEM_NAME))
                .andExpect(jsonPath("$[0].description").value(ITEM_DESCRIPTION))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(itemService).searchSuitableItems(eq("name"));
    }

    @Test
    void searchItemsWhenEmptyTextTest() throws Exception {
        when(itemService.searchSuitableItems(eq("")))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemService).searchSuitableItems(eq(""));
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(eq(1L), any()))
                .thenReturn(itemDto);

        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName(ITEM_NAME);
        newItemRequest.setDescription(ITEM_DESCRIPTION);
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(ITEM_NAME))
                .andExpect(jsonPath("$.description").value(ITEM_DESCRIPTION))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner").value(1));

        verify(itemService).createItem(eq(1L), any());
    }

    @Test
    void addNewCommentTest() throws Exception {
        when(itemService.addNewComment(eq(1L), eq(1L), any()))
                .thenReturn(commentDto);

        NewCommentRequest newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("text");

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.authorName").value("Oliver"))
                .andExpect(jsonPath("$.created").exists());

        verify(itemService).addNewComment(eq(1L), eq(1L), any());
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(eq(1L), eq(1L), any()))
                .thenReturn(itemDto);

        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName(ITEM_NAME);
        updateItemRequest.setDescription(ITEM_DESCRIPTION);
        updateItemRequest.setAvailable(true);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(ITEM_NAME))
                .andExpect(jsonPath("$.description").value(ITEM_DESCRIPTION))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner").value(1));

        verify(itemService).updateItem(eq(1L), eq(1L), any());
    }

    @Test
    void updateItemWhenNotOwnerTest() throws Exception {
        when(itemService.updateItem(eq(2L), eq(1L), any()))
                .thenThrow(new NonAuthorizedUserException("Не владелец вещи"));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName(ITEM_NAME);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.description").value("Не владелец вещи"));

        verify(itemService).updateItem(eq(2L), eq(1L), any());
    }
}