package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.dto.RequestStatuses;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.stats.mapper.EndpointHitMapper;
import ru.practicum.ewm.stats.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventService {

    private final EventRepository eventRepository;

    private final RequestRepository requestsRepository;

    private final StatsService statService;

    public List<ShortEventDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable,
                                          HttpServletRequest request) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeStart != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (text == null) text = "";
        List<Event> events = eventRepository.findByParamsOrderByDate(text.toLowerCase(), List.of(EventState.PUBLISHED),
                categories, paid, start, end, pageable);
        List<FullEventDto> fullEventDtoList = events.stream()
                .map(EventMapper::toFullEventDto)
                .collect(Collectors.toList());
        fullEventDtoList.forEach(event -> event.setConfirmedRequests(requestsRepository
                .findConfirmedRequests(event.getId()).size()));
        if (onlyAvailable) {
            fullEventDtoList = fullEventDtoList.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }
        statService.create(new EndpointHitDto(null, "ewm-main",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));
        List<ShortEventDto> eventsShort = EventUtil.getViews(fullEventDtoList, statService).stream()
                .map(EventMapper::toShortFromFull)
                .collect(Collectors.toList());
        if (sort != null && sort.equalsIgnoreCase("VIEWS")) {
            eventsShort.sort((e1, e2) -> e2.getViews().compareTo(e1.getViews()));
        }
        EventUtil.getConfirmedRequests(fullEventDtoList, requestsRepository);
        return eventsShort;
    }

    public FullEventDto findById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдена");
        });
        FullEventDto fullEventDto = EventMapper.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(requestsRepository.findAllByEventIdAndStatus(event.getId(), RequestStatuses.CONFIRMED).size());
        statService.create(EndpointHitMapper.toEndpointHitDto("ewm-main-service", request));
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }
}
