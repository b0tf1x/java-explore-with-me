package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.UpdateRequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    RequestDto cancel(Long userId, Long requestId);

    List<RequestDto> findByInitiatorAndEvent(Long userId, Long eventId);

    List<RequestDto> findByRequester(Long userId);

    UpdateRequestDto requestProcessing(Long userId, Long eventId,
                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
