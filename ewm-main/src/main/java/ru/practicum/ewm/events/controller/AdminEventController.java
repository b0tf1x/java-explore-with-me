package ru.practicum.ewm.events.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.ewm.events.dto.EventUpdateRequestDto;
import ru.practicum.ewm.events.service.AdminEventService;
import javax.validation.constraints.PositiveOrZero;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.dto.EventState;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminEventService adminEventService;

    @GetMapping
    public List<FullEventDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<EventState> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        return adminEventService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return adminEventService.updateEvent(eventId, eventUpdateRequestDto);
    }
}
