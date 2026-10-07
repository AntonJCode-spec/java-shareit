package ru.practicum.shareit.request;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping("/requests")
@Validated
public class RequestController {
    public final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getRequestByRequestorId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId) {
        return requestClient.getRequest(requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody NewItemRequestDto newItemRequestDto) {
        return requestClient.postRequest(userId, newItemRequestDto);
    }
}
