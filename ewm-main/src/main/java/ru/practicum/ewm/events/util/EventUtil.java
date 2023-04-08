package ru.practicum.ewm.events.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.EventDto;
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
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);
    public static final LocalDateTime MAX_TIME = toTime("5000-01-01 00:00:00");
    public static final LocalDateTime MIN_TIME = toTime("2000-01-01 00:00:00");

    public static <T extends EventDto> List<T> getViews(List<T> eventDtos, StatsService statsService) {
        Map<String, T> views = eventDtos.stream()
                .collect(Collectors.toMap(eventDto -> "/events/" + eventDto.getId(),
                        eventDto -> eventDto));
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

    public static <T extends EventDto> void getConfirmedRequests(List<T> eventDtos,
                                                                 RequestRepository requestsRepository) {
        List<Long> ids = eventDtos.stream()
                .map(EventDto::getId)
                .collect(Collectors.toList());
        List<Request> requests = requestsRepository.findConfirmedRequestsByIds(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDtos.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public static LocalDateTime toTime(String timeString) throws DateTimeParseException {
        return LocalDateTime.parse(timeString, FORMATTER);
    }
}
