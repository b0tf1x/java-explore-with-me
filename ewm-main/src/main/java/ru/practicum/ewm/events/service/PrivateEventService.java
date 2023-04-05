package ru.practicum.ewm.events.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.List;

public interface PrivateEventService {
    EventFullDto create(Long userId,  CreateEventDto createEventDto);
    List<EventShortDto> getByCreator(Long userId, Pageable pageable);
}
