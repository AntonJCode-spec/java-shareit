package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void shouldDeleteUser() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Oliver");
        newUserRequest.setEmail("oliver@mail.ru");
        UserDto created = userService.createUser(newUserRequest);

        userService.deleteUser(created.getId());

        Optional<User> deleted = userRepository.findById(created.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void shouldFindAllUsers() {
        UserDto user1 = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));
        UserDto user2 = userService.createUser(createUserRequest("John", "john@mail.ru"));

        Collection<UserDto> users = userService.findAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("oliver@mail.ru", "john@mail.ru");
    }

    @Test
    void shouldFindUserById() {
        UserDto created = userService.createUser(createUserRequest("Oliver", "oliver@mail.ru"));

        UserDto found = userService.findUserById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Oliver");
        assertThat(found.getEmail()).isEqualTo("oliver@mail.ru");
    }

    @Test
    void shouldThrowWhenFindNotExistsUser() {
        assertThatThrownBy(() -> userService.findUserById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    private NewUserRequest createUserRequest(String name, String email) {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName(name);
        newUserRequest.setEmail(email);

        return newUserRequest;
    }
}
