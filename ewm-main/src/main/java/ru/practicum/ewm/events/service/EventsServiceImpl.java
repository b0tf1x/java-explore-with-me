package ru.practicum.ewm.events.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.events.repository.LocationRepository;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.mapper.EventMapper.FORMATTER;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final CategoriesRepository categoriesRepository;
    private final LocationRepository locationRepository;
    private final EntityManager entityManager;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> findEventsAdmin(List<Long> users, List<EventStatuses> states, List<Long> categories,
                                              String rangeStart, String rangeEnd, Integer from, Integer size) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> eventCriteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = eventCriteriaQuery.from(Event.class);
        List<Predicate> predicateList = new ArrayList<>();

        TypedQuery<Event> typedQuery = entityManager.createQuery(eventCriteriaQuery);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        if (users != null) {
            predicateList.add(criteriaBuilder.and(eventRoot.get("initiator").get("id").in(users)));
        }

        if (categories != null) {
            predicateList.add(criteriaBuilder.and(eventRoot.get("category").get("id").in(categories)));
        }

        if (states != null) {
            predicateList.add(criteriaBuilder.and(states.stream().map(eventState -> criteriaBuilder
                    .equal(eventRoot.get("eventState"), eventState)).toArray(Predicate[]::new)));
        }

        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            predicateList.add(criteriaBuilder.and(criteriaBuilder.between(eventRoot.get("eventDate"), start, end)));
        }
        List<EventFullDto> events = entityManager.createQuery(eventCriteriaQuery.select(eventRoot)
                        .where(predicateList.toArray(Predicate[]::new))).getResultList().stream()
                .map(EventMapper::eventFullDto)
                .collect(Collectors.toList());
        addViews(events, statsClient);
        addConfirmedRequests(events, requestRepository);
        return events;
    }

    @Transactional
    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoriesRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> {
                throw new NotFoundException("Категория не найдена");
            });
            event.setCategory(category);
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventAdminRequest.getEventDate(), FORMATTER));
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (event.getEventStatuses() != EventStatuses.PENDING) {
                throw new BadRequestException("Неправильный статус");
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStatuses.PUBLISHED.name())) {
                event.setEventStatuses(EventStatuses.PUBLISHED);
            }
        }
        event = eventsRepository.save(event);
        addViews(List.of(EventMapper.eventFullDto(event)), statsClient);
        addConfirmedRequests(List.of(EventMapper.eventFullDto(event)), requestRepository);
        return EventMapper.eventFullDto(event);
    }

    @Override
    public List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable,
                                          HttpServletRequest httpServletRequest) {
        sendStatistics(httpServletRequest);
        /**LocalDateTime start = null;
         LocalDateTime end = null;
         if (rangeStart != null) {
         start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         }
         if (rangeStart != null) {
         end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         }
         if (text == null) {
         text = "";
         }
         List<Event> events = eventsRepository.findByParams(text.toLowerCase(), List.of(EventStatuses.PUBLISHED),
         categories, paid, start, end, pageable);
         List<EventFullDto> fullEventDtoList = events.stream()
         .map(EventMapper::eventFullDto)
         .collect(Collectors.toList());
         fullEventDtoList.forEach(event -> event.setConfirmedRequests(requestRepository
         .findConfirmedRequests(event.getId()).size()));
         if (onlyAvailable) {
         fullEventDtoList = fullEventDtoList.stream()
         .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
         .collect(Collectors.toList());
         }
         statsService.create(EndpointHitMapper.toEndpointHitDto("ewm-main-service", httpServletRequest));
         List<EventShortDto> eventsShort = EventUtil.getViews(fullEventDtoList, statService).stream()
         .map(EventMapper::toShortDto)
         .collect(Collectors.toList());
         if (sort != null && sort.equalsIgnoreCase("VIEWS")) {
         eventsShort.sort((e1, e2) -> e2.getViews().compareTo(e1.getViews()));
         }
         EventUtil.getConfirmedRequests(fullEventDtoList, requestsRepository);
         log.info("Events sent");
         return eventsShort;**/
        LocalDateTime start = LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER);
        LocalDateTime end = LocalDateTime.parse("5000-01-01 00:00:00", FORMATTER);
        List<EventFullDto> events = eventsRepository
                .findByParams(text.toLowerCase(), List.of(EventStatuses.PUBLISHED),
                        categories, paid, start, end, pageable)
                .stream()
                .map(EventMapper::eventFullDto)
                .map(event -> {
                    event.setConfirmedRequests(requestRepository.findConfirmedRequests(event.getId()).size());
                    return event;
                })
                .collect(Collectors.toList());

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (sort.equals("VIEWS")) {
            events.sort((event1, event2) -> Long.compare(event2.getViews(), event1.getViews()));
        }

        addViews(events, statsClient);
        addConfirmedRequests(events, requestRepository);
        return events
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findById(Long eventId, HttpServletRequest httpServletRequest) {
        sendStatistics(httpServletRequest);
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
        if (event.getEventStatuses() != EventStatuses.PUBLISHED) {
            throw new NotFoundException("Событие не найдено");
        }

        EventFullDto eventFullDto = EventMapper.eventFullDto(event);
        addViews(List.of(eventFullDto), statsClient);
        addConfirmedRequests(List.of(eventFullDto), requestRepository);
        int confirmedRequests = requestRepository.findConfirmedRequests(eventId).size();
        eventFullDto.setConfirmedRequests(confirmedRequests);
        return eventFullDto;
    }

    public static void addViews(List<EventFullDto> events, StatsClient statsClient) {
        Map<String, EventFullDto> eventsMap = events.stream()
                .collect(Collectors.toMap(event -> "/events" + event.getId(), event -> event));
        Object statistics = statsClient.getViewStats(
                LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER).format(FORMATTER),
                LocalDateTime.parse("5000-01-01 00:00:00", FORMATTER).format(FORMATTER),
                new ArrayList<>(eventsMap.keySet()),
                false
        ).getBody();
        List<ViewStatsDto> statsList = new ObjectMapper().convertValue(statistics, new TypeReference<>() {
        });
        statsList.forEach(stat -> {
            if (eventsMap.containsKey(stat.getUri())) {
                eventsMap.get(stat.getUri()).setViews(stat.getHits());
            }
        });
    }

    private void sendStatistics(HttpServletRequest httpServletRequest) {
        statsClient.create(new EndpointHitDto(null, "ewm-main",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now()));
    }

    public static void addConfirmedRequests(List<EventFullDto> events, RequestRepository requestRepository) {
        Map<Long, Integer> requestsCountMap = new HashMap<>();

        List<Request> requests = requestRepository.findConfirmedRequestsByIds(events
                .stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList())
        );

        requests.forEach(request -> {
            long eventId = request.getEvent().getId();

            if (!requestsCountMap.containsKey(eventId)) {
                requestsCountMap.put(eventId, 0);
            }

            requestsCountMap.put(eventId, requestsCountMap.get(eventId) + 1);
        });

        events.forEach(event -> {
            if (requestsCountMap.containsKey(event.getId())) {
                event.setConfirmedRequests(requestsCountMap.get(event.getId()));
            }
        });
    }
}
