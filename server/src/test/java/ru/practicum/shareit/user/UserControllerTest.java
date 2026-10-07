package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(
                1L,
                "Oliver",
                "oliver@mail.ru"
        );
    }

    @Test
    void createNewUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Oliver");
        newUserRequest.setEmail("oliver@mail.ru");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oliver"))
                .andExpect(jsonPath("$.email").value("oliver@mail.ru"));
    }

    @Test
    void createUserWithDuplicateEmailTest() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new DuplicateDataException("Email уже занят"));

        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Oliver");
        newUserRequest.setEmail("oliver@mail.ru");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.description").value("Email уже занят"));

        verify(userService).createUser(any());
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUserField(eq(1L), any()))
                .thenReturn(userDto);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Oliver");
        updateUserRequest.setEmail("oliver@mail.ru");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oliver"))
                .andExpect(jsonPath("$.email").value("oliver@mail.ru"));
        verify(userService).updateUserField(eq(1L), any());
    }

    @Test
    void updateUserWithDuplicateEmailTest() throws Exception {
        when(userService.updateUserField(eq(1L), any()))
                .thenThrow(new DuplicateDataException("Email уже занят"));

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Oliver");
        updateUserRequest.setEmail("oliver@mail.ru");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.description").value("Email уже занят"));

        verify(userService).updateUserField(eq(1L), any());
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
        verify(userService).deleteUser(eq(1L));
    }

    @Test
    void deleteNotExistsUserTest() throws Exception {
        doThrow(new UserNotFoundException("Пользователь не найден"))
                .when(userService).deleteUser(eq(100L));

        mvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.description").value("Пользователь не найден"));

        verify(userService).deleteUser(eq(100L));
    }

    @Test
    void getUsersTest() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Oliver"))
                .andExpect(jsonPath("$[0].email").value("oliver@mail.ru"));

        verify(userService).findAllUsers();
    }

    @Test
    void getNotExistsUserTest() throws Exception {
        when(userService.findUserById(eq(100L)))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));

        mvc.perform(get("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.description").value("Пользователь не найден"));

        verify(userService).findUserById(eq(100L));
    }

    @Test
    void getUsersByIdTest() throws Exception {
        when(userService.findUserById(eq(1L)))
                .thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oliver"))
                .andExpect(jsonPath("$.email").value("oliver@mail.ru"));

        verify(userService).findUserById(eq(1L));
    }

}
