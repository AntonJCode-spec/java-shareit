package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.values();
    }

    @Override
    public boolean delete(Long id) {
        return userStorage.remove(id) != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return userStorage.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isUserExist(Long id) {
        return userStorage.containsKey(id);
    }

    private Long getNextId() {
        return userStorage.keySet().stream().max(Long::compareTo).orElse(1L) + 1L;
    }
}
