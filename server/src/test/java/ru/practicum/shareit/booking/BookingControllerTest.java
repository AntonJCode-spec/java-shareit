package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.ItemToBookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    private static final LocalDateTime START_TIME = LocalDateTime.of(2030, 1, 1, 10, 0);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2030, 1, 10, 10, 0);

    @BeforeEach
    void beforeEach() {
        ItemToBookingDto item = new ItemToBookingDto(1L, "Дрель");
        BookerDto booker = new BookerDto(1L);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(START_TIME);
        bookingDto.setEnd(END_TIME);
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(BookingStatus.WAITING);
    }

    @Test
    void getBookingByBookerIdTest() throws Exception {
        when(bookingService.findUserBookings(eq(1L), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].item").isNotEmpty())
                .andExpect(jsonPath("$[0].booker").isNotEmpty());
        verify(bookingService).findUserBookings(eq(1L), eq(BookingState.ALL));
    }

    @Test
    void getOwnerBookingsTest() throws Exception {
        when(bookingService.findOwnerBookings(eq(1L), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].item").isNotEmpty())
                .andExpect(jsonPath("$[0].booker").isNotEmpty());
        verify(bookingService).findOwnerBookings(eq(1L), eq(BookingState.ALL));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(eq(1L), eq(1L)))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty());
        verify(bookingService).getBookingById(eq(1L), eq(1L));
    }

    @Test
    void postBookingTest() throws Exception {
        when(bookingService.createBooking(eq(1L), any()))
                .thenReturn(bookingDto);

        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(START_TIME);
        newBookingRequest.setEnd(END_TIME);
        newBookingRequest.setItemId(1L);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(newBookingRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty());
        verify(bookingService).createBooking(eq(1L), any());
    }

    @Test
    void reviewBookingTest() throws Exception {
        when(bookingService.reviewBooking(eq(1L), eq(1L), anyBoolean()))
                .thenReturn(bookingDto);

        bookingDto.setStatus(BookingStatus.APPROVED);
        mvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        verify(bookingService).reviewBooking(eq(1L), eq(1L), anyBoolean());
    }
}
