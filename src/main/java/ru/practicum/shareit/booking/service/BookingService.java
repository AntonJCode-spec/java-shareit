package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.util.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.DateTimeValidationException;
import ru.practicum.shareit.exception.NonAuthorizedUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирования не существует"));

        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.mapToBookingDto(booking);
        } else {
            throw new NonAuthorizedUserException("Информацию о бронировании может запрашивать владелец вещи" +
                    " либо автор бронирования");
        }
    }

    public BookingDto createBooking(Long bookerId, NewBookingRequest newBookingRequest) {
        if (!isDateValid(newBookingRequest.getStart(), newBookingRequest.getEnd())) {
            throw new DateTimeValidationException("Некорректный интервал брони");
        }
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(newBookingRequest.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь для бронирования не найдена"));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь не доступна к бронированию");
        }

        Booking booking = BookingMapper.mapToBooking(user, item, newBookingRequest);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    public BookingDto reviewBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирования не существует"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new DataValidationException("Бронирование не находится в статусе WAITING");
        }

        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new NonAuthorizedUserException("Пользователь не является владельцем вещи");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    public Collection<BookingDto> findUserBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, now);
            case PAST -> bookingRepository.findPastByBookerId(userId, now);
            case FUTURE -> bookingRepository.findFutureByBookerId(userId, now);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    public Collection<BookingDto> findOwnerBookings(Long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(ownerId, now);
            case PAST -> bookingRepository.findPastByOwnerId(ownerId, now);
            case FUTURE -> bookingRepository.findFutureByOwnerId(ownerId, now);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    private boolean isDateValid(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(end) && start.isAfter(LocalDateTime.now());
    }

}
