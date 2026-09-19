package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Collection<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        User userToAdd = UserMapper.mapToUser(newUserRequest);

        if (userRepository.existsByEmail(userToAdd.getEmail())) {
            throw new DuplicateDataException("Пользователь с указанным email уже существует");
        }
        return UserMapper.mapToUserDto(userRepository.save(userToAdd));
    }

    public UserDto updateUserField(Long id, UpdateUserRequest updateUserRequest) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с указанным id не существует"));

        if (!updatedUser.getEmail().equals(updateUserRequest.getEmail())) {
            if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new DuplicateDataException("Пользователь с полученным email уже существует");
            }
        }
        UserMapper.updateFields(updatedUser, updateUserRequest);

        userRepository.save(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
