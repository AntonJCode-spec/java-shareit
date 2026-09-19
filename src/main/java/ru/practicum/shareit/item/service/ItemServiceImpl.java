package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.util.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NonAuthorizedUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.util.CommentMapper;
import ru.practicum.shareit.util.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    public Collection<ItemDtoForOwner> getItemsByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemsId = items.stream().map(Item::getId).toList();
        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookings = bookingRepository
                .findLastBookingsForItems(itemsId, now)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (b1, b2) -> b1
                ));

        Map<Long, Booking> nextBookings = bookingRepository
                .findNextBookingsForItems(itemsId, now)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (b1, b2) -> b1
                ));

        return items.stream().map(item -> ItemMapper.mapToItemDtoForOwner(item,
                lastBookings.get(item.getId()),
                nextBookings.get(item.getId())))
                .toList();

    }

    @Override
    public ItemWithComments getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с таким id не существует"));

        List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        BookingDateDto lastBooking = null;
        BookingDateDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            lastBooking = bookingRepository.findLastBookingForItem(item.getId(), now)
                    .map(BookingMapper::mapToBookingDate)
                    .orElse(null);

            nextBooking = bookingRepository.findNextBookingForItem(item.getId(), now)
                    .map(BookingMapper::mapToBookingDate)
                    .orElse(null);
        }

        return ItemMapper.mapToItemWithComments(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemDto> searchSuitableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Long userId, NewItemRequest newItemRequest) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с переданным id не существует"));
        Item itemToAdd = ItemMapper.mapToItem(newItemRequest);
        itemToAdd.setOwner(owner);

        return ItemMapper.mapToItemDto(itemRepository.save(itemToAdd));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещи с переданным id не существует"));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new NonAuthorizedUserException("Обновлять данные о вещи может только её владелец");
        }

        ItemMapper.updateField(itemToUpdate, updateItemRequest);

        itemRepository.save(itemToUpdate);


        return ItemMapper.mapToItemDto(itemToUpdate);
    }

    @Override
    public CommentDto addNewComment(Long authorId, Long itemId, NewCommentRequest newCommentRequest) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));

        Booking booking = bookingRepository.findLastApprovedBooking(authorId, itemId)
                .orElseThrow(() -> new DataValidationException("Можно комментировать только арендованные ранее вещи"));
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new DataValidationException("Аренда ещё не закончена");
        }

        Comment comment = new Comment();
        comment.setText(newCommentRequest.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }
}
