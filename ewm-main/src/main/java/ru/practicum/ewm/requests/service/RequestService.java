package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    List<RequestDto> findByInitiatorId(Long userId);

    RequestDto cancel(Long userId, Long requestId);

    List<RequestDto> findByInitiatorAndEvent(Long userId, Long eventId);

    List<RequestDto> findByRequester(Long userId);
}
