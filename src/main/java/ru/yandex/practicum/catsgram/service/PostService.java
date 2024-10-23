package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его нужно добавить в контекст приложения
@Service   // == @Component
@RequiredArgsConstructor
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    private final UserService userService;

    public Collection<Post> findAll(int from, int size, SortOrder sortOrder) {
        return posts.values().stream().sorted((p1, p2) -> {
            int comp = p1.getPostDate().compareTo(p2.getPostDate()); //прямой порядок сортировки
            return sortOrder == SortOrder.ASCENDING ? comp : -comp;
        }).skip(from).limit(size).collect(Collectors.toList());
    }
/*
    public Collection<Post> findAll(int from, int size, SortOrder sortOrder) {
        List<Post> postList = new ArrayList<>(posts.values());
        if (sortOrder == SortOrder.ASCENDING) {
            postList.sort(Comparator.comparing(Post::getPostDate, nullsFirst(naturalOrder())));
        } else if (sortOrder == SortOrder.DESCENDING) {
            postList.sort(Comparator.comparing(Post::getPostDate, nullsFirst(naturalOrder())).reversed());
        }
        ArrayList<Post> filteredPosts = new ArrayList<>();
        for (int index = from; index < from + size && index < postList.size(); index++) {
            filteredPosts.add(postList.get(index));
        }
        return filteredPosts;
    }
*/

    public Optional<Post> findById(Long postId) {
        return posts.values().stream()
                .filter(p -> p.getId() == postId)
                .findFirst();
    }
/*
    public Optional<Post> findById(Long id) {
        if (posts.containsKey(id)) {
            return Optional.of(posts.get(id));
        } else {
            //throw new NotFoundException(String.format("Пост № %d не найден", id));
            return Optional.empty()
        }
    }
*/

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        if (userService.findById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException(String.format("Автор с id=%d не найден", post.getAuthorId()));
        }
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}