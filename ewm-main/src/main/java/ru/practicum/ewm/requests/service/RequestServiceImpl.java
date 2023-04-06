package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestStatuses;
import ru.practicum.ewm.requests.dto.UpdateRequestDto;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public RequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие не опубликовано");
        }
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictException("Нельзя отправить запрос на своё мероприятие");
        }
        int confirmedRequests = requestRepository.findConfirmedRequests(eventId).size();
        if (event.getParticipantLimit() <= confirmedRequests && event.getParticipantLimit() != 0) {
            throw new ConflictException("Запросы превышают лимит");
        }
        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new ConflictException("Можно отправить только один запрос");
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
        Request request = requestRepository.findByRequesterAndRequest(userId, requestId).orElseThrow(() -> {
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

    @Override
    public List<RequestDto> findByRequester(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestsMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public UpdateRequestDto requestProcessing(Long userId, Long eventId,
                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("You don't have event with id " + eventId);
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Confirmation is not required");
        }
        UpdateRequestDto requestUpdateDto = new UpdateRequestDto(new ArrayList<>(), new ArrayList<>());
        Integer confirmedRequests = requestRepository.findConfirmedRequests(eventId).size();
        List<Request> requests = requestRepository.findByEventIdAndRequestsIds(eventId,
                eventRequestStatusUpdateRequest.getRequestIds());
        if (Objects.equals(eventRequestStatusUpdateRequest.getStatus(), RequestStatuses.CONFIRMED.name())
                && confirmedRequests + requests.size() > event.getParticipantLimit()) {
            requests.forEach(request -> request.setStatus(RequestStatuses.REJECTED));
            List<RequestDto> requestDto = requests.stream()
                    .map(RequestsMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestDto);
            requestRepository.saveAll(requests);
            throw new ConflictException("Requests limit exceeded");
        }
        if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatuses.REJECTED.name())) {
            requests.forEach(request -> {
                if (request.getStatus().equals(RequestStatuses.CONFIRMED)) {
                    throw new ConflictException("You can't reject confirmed request");
                }
                request.setStatus(RequestStatuses.REJECTED);
            });
            List<RequestDto> requestDto = requests.stream()
                    .map(RequestsMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestDto);
            requestRepository.saveAll(requests);
        } else if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatuses.CONFIRMED.name())
                && eventRequestStatusUpdateRequest.getRequestIds().size() <= event.getParticipantLimit() - confirmedRequests) {
            requests.forEach(request -> request.setStatus(RequestStatuses.CONFIRMED));
            List<RequestDto> requestDto = requests.stream()
                    .map(RequestsMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setConfirmedRequests(requestDto);
            requestRepository.saveAll(requests);
        }
        return requestUpdateDto;
    }
}
