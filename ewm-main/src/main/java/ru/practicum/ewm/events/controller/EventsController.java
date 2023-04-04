package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.service.EventsService;
import ru.practicum.ewm.requests.dto.UpdateEventAdminRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventsController {
    private final EventsService eventsService;

    @GetMapping("/admin/events")
    public List<EventFullDto> findEventsAdmin(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<EventStatuses> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "20") Integer size) {
        return eventsService.findEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto update(@PathVariable Long eventId, @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventsService.update(eventId, updateEventAdminRequest);
    }

    @GetMapping("/events")
    public List<EventShortDto> findEvents(@RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          HttpServletRequest httpServletRequest) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return eventsService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageable, httpServletRequest);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto findById(@PathVariable long eventId,
                                  HttpServletRequest httpServletRequest) {
        return eventsService.findById(eventId, httpServletRequest);
    }
}
