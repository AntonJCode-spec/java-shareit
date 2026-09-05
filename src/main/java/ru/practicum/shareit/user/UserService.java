package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserDto> findAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        return userStorage.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
    }

    public UserDto createUser(UserDto userDto) {
        User userToAdd = UserMapper.mapToUser(userDto);
        if (userToAdd.getEmail() == null || userToAdd.getEmail().isBlank()) {
            throw new DataValidationException("Email не может быть пустым");
        }
        validateEmail(userToAdd.getEmail());
        if (userToAdd.getName() == null || userToAdd.getName().isBlank()) {
            throw new DataValidationException("Имя пользователя не может быть пустым");
        }

        if (userStorage.isUserExist(userToAdd)) {
            throw new DuplicateDataException("Пользователь с указанным email уже существует");
        }
        return UserMapper.mapToUserDto(userStorage.create(userToAdd));
    }

    public UserDto updateUserField(Long id, UserDto userDto) {
        User updatedUser = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с указанным id не существует"));

        if (!updatedUser.getEmail().equals(userDto.getEmail())) {
            if (userStorage.isUserExist(UserMapper.mapToUser(userDto))) {
                throw new DuplicateDataException("Пользователь с полученным email уже существует");
            }
        }
        UserMapper.updateFields(updatedUser, userDto);
        validateEmail(updatedUser.getEmail());

        userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userStorage.delete(id)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
    }

    private void validateEmail(String email) {
        if (email != null && !email.contains("@")) {
            throw new DataValidationException("Некорректный email");
        }
    }


}
