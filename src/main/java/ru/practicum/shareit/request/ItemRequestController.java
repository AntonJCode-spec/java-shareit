package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestOnItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestDto> getRequestByRequestorId(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return itemRequestService.getUserRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllOtherUsersRequests(userId);
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) Long requestorId,
                                        @RequestBody NewRequestOnItemDto newRequestOnItemDto) {
        return itemRequestService.createRequest(requestorId, newRequestOnItemDto);
    }
}
