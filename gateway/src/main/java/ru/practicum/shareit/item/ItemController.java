package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {

        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody NewItemRequest newItemRequest) {
        return itemClient.postItem(userId, newItemRequest);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemClient.postComment(authorId, itemId, newCommentRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @PathVariable Long itemId,
                                            @Valid @RequestBody UpdateItemRequest updateItemRequest) {
        return itemClient.patchItem(userId, itemId, updateItemRequest);
    }
}
