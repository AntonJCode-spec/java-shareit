package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUserTest() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Oliver");
        newUserRequest.setEmail("oliver@mail.ru");

        UserDto created = userService.createUser(newUserRequest);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Oliver");
        assertThat(created.getEmail()).isEqualTo("oliver@mail.ru");

        Optional<User> saved = userRepository.findById(created.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getName()).isEqualTo("Oliver");
        assertThat(saved.get().getEmail()).isEqualTo("oliver@mail.ru");
    }

    @Test
    void shouldUpdateUserTest() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Oliver");
        newUserRequest.setEmail("oliver@mail.ru");
        UserDto userDto = userService.createUser(newUserRequest);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("NewOliver");
        updateUserRequest.setEmail("newoliver@mail.ru");

        userService.updateUserField(userDto.getId(), updateUserRequest);
        Optional<User> updatedUser = userRepository.findById(userDto.getId());

        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo(updateUserRequest.getName());
        assertThat(updatedUser.get().getEmail()).isEqualTo(updateUserRequest.getEmail());

    }
}
