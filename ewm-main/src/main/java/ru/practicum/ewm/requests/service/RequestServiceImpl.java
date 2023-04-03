package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestStatuses;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public RequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User not found");
        });
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Event not found");
        });
        if (event.getEventStatuses() != EventStatuses.PUBLISHED) {
            throw new BadRequestException("Событие не опубликовано");
        }
        if (event.getInitiator().getId() == userId) {
            throw new BadRequestException("Нельзя отправить запрос на своё мероприятие");
        }
        int confirmedRequests = requestRepository.findConfirmedRequests(eventId).size();
        if (event.getParticipantLimit() < confirmedRequests && event.getParticipantLimit() != 0) {
            throw new BadRequestException("Confirmed out of limit");
        }
        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new BadRequestException("You can send only one request");
        }
        RequestStatuses requestStatus = RequestStatuses.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = RequestStatuses.CONFIRMED;
        }
        Request request = new Request(null,
                LocalDateTime.now(),
                event,
                user,
                requestStatus);
        request = requestRepository.save(request);
        return RequestsMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> findByInitiatorId(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestsMapper::toRequestDto)
                .collect(Collectors.toList());
    }
    @Override
    public RequestDto cancel(Long userId, Long requestId) {
        Request request = requestRepository.findByInitiatorAndRequest(userId, requestId).orElseThrow(() -> {
            throw new NotFoundException("Request not found");
        });
        request.setStatus(RequestStatuses.CANCELED);
        request = requestRepository.save(request);
        return RequestsMapper.toRequestDto(request);
    }
    @Override
    public List<RequestDto> findByInitiatorAndEvent(Long userId, Long eventId) {
        return requestRepository.findByInitiatorAndEvent(userId, eventId).stream()
                .map(RequestsMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
