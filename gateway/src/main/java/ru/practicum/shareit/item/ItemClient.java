package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()

        );
    }

    public ResponseEntity<Object> getItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        Map<String, Object> params = Map.of(
                "itemId", itemId
        );
        return get("/{itemId}", userId, params);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> params = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, params);
    }

    public ResponseEntity<Object> postItem(Long userId, NewItemRequest newItemRequest) {
        return post("", userId, newItemRequest);
    }

    public ResponseEntity<Object> postComment(Long authorId, Long itemId, NewCommentRequest newCommentRequest) {
        Map<String, Object> params = Map.of(
                "itemId", itemId
        );

        return post("/{itemId}/comment", authorId, params, newCommentRequest);
    }

    public ResponseEntity<Object> patchItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        return patch("/" + itemId, userId, updateItemRequest);
    }
}
