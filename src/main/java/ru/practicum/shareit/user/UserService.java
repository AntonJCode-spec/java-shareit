package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    public UserDto createUser(NewUserRequest newUserRequest) {
        User userToAdd = UserMapper.mapToUser(newUserRequest);

        if (userStorage.isEmailExist(userToAdd.getEmail())) {
            throw new DuplicateDataException("Пользователь с указанным email уже существует");
        }
        return UserMapper.mapToUserDto(userStorage.create(userToAdd));
    }

    public UserDto updateUserField(Long id, UpdateUserRequest updateUserRequest) {
        User updatedUser = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с указанным id не существует"));

        if (!updatedUser.getEmail().equals(updateUserRequest.getEmail())) {
            if (userStorage.isEmailExist(updateUserRequest.getEmail())) {
                throw new DuplicateDataException("Пользователь с полученным email уже существует");
            }
        }
        UserMapper.updateFields(updatedUser, updateUserRequest);

        userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userStorage.delete(id)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
    }
}
