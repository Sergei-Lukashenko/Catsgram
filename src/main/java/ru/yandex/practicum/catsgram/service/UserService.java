package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        // проверяем выполнение необходимых условий
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    public User update(User putUser) {
        // проверяем необходимые условия
        Long putId = putUser.getId();
        if (putId == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = users.get(putId);
        String oldEmail = oldUser.getEmail();

        String putEmail = putUser.getEmail();
        if (putEmail == null || putEmail.isBlank()) {
            putUser.setEmail(oldEmail);
        } else if (!putEmail.equals(oldEmail)
                && users.values().stream()
                .anyMatch(u -> u.getId() != putId && u.getEmail().equals(putEmail))
        ) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (putUser.getUsername() == null || putUser.getUsername().isBlank()) {
            putUser.setUsername(oldUser.getUsername());
        }
        if (putUser.getPassword() == null || putUser.getPassword().isBlank()) {
            putUser.setPassword(oldUser.getPassword());
        }

        if (!users.containsKey(putId)) {
            throw new NotFoundException("Пользователь с id = " + putId + " не найден");
        }

        // если пользователь найден и все условия соблюдены, обновляем его
        users.put(putId, putUser);
        return putUser;
    }

    public Optional<User> findById(Long userId) {
        return users.values().stream()
                .filter(u -> u.getId() == userId)
                .findFirst();
    }
/*
    public Optional<User> findById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        } else {
            return Optional.empty();
        }
    }
*/

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
