package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventUpdateRequestDto;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.repository.LocationRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.dto.RequestStatuses;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.stats.service.StatsService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrivateEventService {

    private final RequestRepository requestsRepository;

    private final LocationRepository locationRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoriesRepository categoriesRepository;

    private final StatsService statService;

    public FullEventDto create(Long userId, CreateEventDto createEventDto) {
        if (createEventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Ошибка в дате");
        }
        User initiator = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        Category category = categoriesRepository.findById(createEventDto.getCategory()).orElseThrow(() -> {
            throw new NotFoundException("Категория не найдена");
        });
        createEventDto.setLocation(locationRepository.save(createEventDto.getLocation()));
        Event event = eventRepository.save(EventMapper.toEventFromCreateDto(initiator, category, createEventDto));
        FullEventDto fullEventDto = EventMapper.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(0);
        return fullEventDto;
    }

    public List<ShortEventDto> getEventsByCreator(Long userId, PageRequest pageable) {
        List<ShortEventDto> shortEventDtos = eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper::toShortEventDto)
                .collect(Collectors.toList());
        EventUtil.getConfirmedRequestsToShort(shortEventDtos, requestsRepository);
        return EventUtil.getViews(shortEventDtos, statService);
    }

    public FullEventDto getEventInfoByCreator(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow();

        FullEventDto fullEventDto = EventMapper.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(requestsRepository
                .findAllByEventIdAndStatus(eventId, RequestStatuses.CONFIRMED).size());
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }

    @Transactional
    public FullEventDto updateEventByCreator(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Нельзя обновить мероприятие");
        }
        if (eventUpdateRequestDto.getEventDate() != null) {
            LocalDateTime time = LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (LocalDateTime.now().isAfter(time.minusHours(2))) {
                throw new ConflictException("Event starts in less then 2 hours");
            }
        }
        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You can't update published event");
        }
        if (eventUpdateRequestDto.getCategory() != null && !Objects.equals(eventUpdateRequestDto.getCategory(),
                event.getCategory().getId())) {
            Category category = categoriesRepository.findById(eventUpdateRequestDto.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (eventUpdateRequestDto.getLocation() != null) {
            Location location = locationRepository.save(eventUpdateRequestDto.getLocation());
            event.setLocation(location);
        }
        EventUtil.toEventFromUpdateRequestDto(event, eventUpdateRequestDto);
        FullEventDto fullEventDto = EventMapper.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(requestsRepository.findAllByEventIdAndStatus(eventId, RequestStatuses.CONFIRMED)
                .size());
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }
}
