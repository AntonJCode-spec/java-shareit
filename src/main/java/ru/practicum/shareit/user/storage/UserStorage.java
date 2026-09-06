package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Optional<User> findById(Long id);

    Collection<User> findAll();

    boolean delete(Long id);

    boolean isEmailExist(String email);

    boolean isUserExist(Long id);
}
