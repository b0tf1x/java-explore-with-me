package ru.practicum.ewm.events.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.requests.dto.UpdateEventAdminRequest;

import java.util.List;

public interface EventsService {
    List<EventFullDto> findEventsAdmin(List<Long> users, List<EventStatuses> states,List<Long> categories,
                                  String rangeStart,String rangeEnd,Integer from, Integer size);
    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
    List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                   String rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable);
    EventShortDto findById(Long eventId);
}
