package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDtoForOwner> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ItemWithComments getItemById(@PathVariable Long id,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findSuitableItems(@RequestParam String text) {
        return itemService.searchSuitableItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody NewItemRequest newItemRequest) {
        return itemService.createItem(userId, newItemRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                    @PathVariable Long itemId,
                                    @RequestBody NewCommentRequest newCommentRequest) {
        return itemService.addNewComment(authorId, itemId, newCommentRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody UpdateItemRequest updateItemRequest) {
        return itemService.updateItem(userId, itemId, updateItemRequest);
    }


}
