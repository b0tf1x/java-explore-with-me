package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/users/{userId}/requests")
public class RequestsController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto create(@PathVariable Long userId,
                             @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> findById(@PathVariable Long userId) {
        return requestService.findByInitiatorId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long userId,
                             @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}
