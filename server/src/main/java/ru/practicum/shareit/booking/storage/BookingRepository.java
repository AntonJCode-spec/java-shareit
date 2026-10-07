package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking AS b
            WHERE b.booker.id = :bookerId
            AND b.status = 'APPROVED'
            AND b.end <= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId,
                                      @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking AS b
            WHERE b.booker.id = :bookerId
            AND b.status = 'APPROVED'
            AND b.start >= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId,
                                       @Param("now") LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("""
            SELECT b FROM Booking AS b
            WHERE b.item.owner.id = :ownerId
            AND b.status = 'APPROVED'
            AND b.start <= :now
            AND b.end >= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking AS b
            WHERE b.item.owner.id = :ownerId
            AND b.status = 'APPROVED'
            AND b.end <= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking AS b
            WHERE b.item.owner.id = :ownerId
            AND b.status = 'APPROVED'
            AND b.start >= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId,
                                      @Param("now") LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id IN :itemIds
        AND b.status = 'APPROVED'
        AND b.end < :now
        ORDER BY b.end DESC
        """)
    List<Booking> findLastBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id IN :itemIds
        AND b.status = 'APPROVED'
        AND b.start > :now
        ORDER BY b.start ASC
        """)
    List<Booking> findNextBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :bookerId
        AND b.item.id = :itemId
        AND b.status = 'APPROVED'
        ORDER BY b.end DESC
        """)
    Optional<Booking> findLastApprovedBooking(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id = :itemId
        AND b.status = 'APPROVED'
        AND b.end < :now
        ORDER BY b.end DESC
        """)
    Optional<Booking> findLastBookingForItem(@Param("itemId") Long itemId,
                                             @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id = :itemId
        AND b.status = 'APPROVED'
        AND b.start > :now
        ORDER BY b.start ASC
        """)
    Optional<Booking> findNextBookingForItem(@Param("itemId") Long itemId,
                                             @Param("now") LocalDateTime now);
}
