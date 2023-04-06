package ru.practicum.ewm.events.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventUpdateRequestDto;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.dto.UserActionState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventUtil {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final LocalDateTime MAX_TIME = toTime("5000-01-01 00:00:00");
    public static final LocalDateTime MIN_TIME = toTime("2000-01-01 00:00:00");

    public static List<FullEventDto> getViews(List<FullEventDto> eventDtos, StatsService statService) {
        Map<String, FullEventDto> views = eventDtos.stream()
                .collect(Collectors.toMap(fullEventDto -> "/events/" + fullEventDto.getId(),
                        fullEventDto -> fullEventDto));
        Object responseBody = statService.getViews(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        List<ViewStatsDto> viewStatsDtos = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDtos.forEach(viewStatsDto -> {
            if (views.containsKey(viewStatsDto.getUri())) {
                views.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    public static List<ShortEventDto> getViewsToShort(List<ShortEventDto> eventDtos, StatsService statsService) {
        Map<String, ShortEventDto> views = eventDtos.stream()
                .collect(Collectors.toMap(fullEventDto -> "/events/" + fullEventDto.getId(),
                        fullEventDto -> fullEventDto));
        Object responseBody = statsService.getViews(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        List<ViewStatsDto> viewStatsDtos = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDtos.forEach(viewStatsDto -> {
            if (views.containsKey(viewStatsDto.getUri())) {
                views.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    public static void getConfirmedRequests(List<FullEventDto> eventDtos,
                                            RequestRepository requestsRepository) {
        List<Long> ids = eventDtos.stream()
                .map(FullEventDto::getId)
                .collect(Collectors.toList());
        List<Request> requests = requestsRepository.findConfirmedRequestsByIds(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDtos.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }

    public static void getConfirmedRequestsToShort(List<ShortEventDto> eventDtos,
                                                   RequestRepository requestsRepository) {
        List<Long> ids = eventDtos.stream()
                .map(ShortEventDto::getId)
                .collect(Collectors.toList());
        List<Request> requests = requestsRepository.findConfirmedRequestsByIds(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDtos.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }
    public static void toEventFromUpdateRequestDto(Event event,
                                                   EventUpdateRequestDto eventUpdateRequestDto) {
        if (Objects.equals(eventUpdateRequestDto.getStateAction(), UserActionState.CANCEL_REVIEW.name())) {
            event.setEventState(EventState.CANCELED);
        }
        if (Objects.equals(eventUpdateRequestDto.getStateAction(), UserActionState.SEND_TO_REVIEW.name())) {
            event.setEventState(EventState.PENDING);
        }
        if (eventUpdateRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDto.getAnnotation());
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
        }
        if (eventUpdateRequestDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (eventUpdateRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateRequestDto.getPaid());
        }
        if (eventUpdateRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateRequestDto.getParticipantLimit());
        }
        if (eventUpdateRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateRequestDto.getRequestModeration());
        }
        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public static LocalDateTime toTime(String timeString) throws DateTimeParseException {
        return LocalDateTime.parse(timeString, FORMATTER);
    }
}
