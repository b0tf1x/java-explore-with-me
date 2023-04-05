package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.service.PrivateEventService;
import ru.practicum.ewm.requests.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {
    private final RequestService requestService;
    private final PrivateEventService privateEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @Valid @RequestBody CreateEventDto createEventDto) {
        return privateEventService.create(userId, createEventDto);
    }

    @GetMapping
    public List<EventShortDto> getByCreator(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "20") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return privateEventService.getByCreator(userId,pageable);
    }
}
