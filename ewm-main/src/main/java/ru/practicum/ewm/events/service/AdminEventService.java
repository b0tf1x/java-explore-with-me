package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.dto.AdminStateAction;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventUpdateRequestDto;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.CriteriaEventRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.repository.LocationRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final CriteriaEventRepository criteriaEventRepository;

    private final EventRepository eventRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CategoriesRepository categoriesRepository;

    private final LocationRepository locationRepository;

    private final RequestRepository requestsRepository;

    private final StatsService statService;

    public List<FullEventDto> findEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                         String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<FullEventDto> fullEventDtoList = criteriaEventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(EventMapper::toFullEventDto)
                .collect(Collectors.toList());
        EventUtil.getConfirmedRequests(fullEventDtoList, requestsRepository);
        return EventUtil.getViews(fullEventDtoList, statService);
    }

    public FullEventDto updateEvent(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
        eventCheckSetDate(eventUpdateRequestDto, event);
        checkStatusExceptions(event, eventUpdateRequestDto);
        eventCheckSetState(eventUpdateRequestDto, event);
        eventCheckSetCategory(eventUpdateRequestDto, event);
        if (eventUpdateRequestDto.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdateRequestDto.getLocation()));
        }
        EventMapper.toEventFromUpdateRequestDto(event, eventUpdateRequestDto);
        System.out.println("event" + event);
        eventRepository.save(event);

        FullEventDto fullEventDto = EventMapper.toFullEventDto(event);
        EventUtil.getConfirmedRequests(Collections.singletonList(fullEventDto), requestsRepository);
        FullEventDto dto = EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
        System.out.println(dto);
        return dto;
    }

    private void eventCheckSetCategory(EventUpdateRequestDto eventUpdateRequestDto, Event event) {
        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoriesRepository.findById(eventUpdateRequestDto.getCategory()).orElseThrow(() -> {
                throw new NotFoundException("Категория не найдена");
            });
            event.setCategory(category);
        }
    }

    private void eventCheckSetDate(EventUpdateRequestDto eventUpdateRequestDto, Event event) {
        if (eventUpdateRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Дата уже прошла");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }
    }

    private void eventCheckSetState(EventUpdateRequestDto eventUpdateRequestDto, Event event) {
        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT.name())) {
                event.setEventState(EventState.PUBLISHED);
            } else if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.REJECT_EVENT.name())
                    && event.getEventState() != EventState.PUBLISHED) {
                event.setEventState(EventState.CANCELED);
            }
        }
    }

    private void checkStatusExceptions(Event event, EventUpdateRequestDto eventUpdateRequestDto) {
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Событие уже создано");
        }
        if (event.getEventState() == EventState.CANCELED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Событие отменено");
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.REJECT_EVENT.name())) {
            throw new ConflictException("Событие отклонено");
        }
    }
}
