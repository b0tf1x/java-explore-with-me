package ru.practicum.ewm.stats.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.ewm.dto.EndpointHitDto;

import java.util.List;

public interface StatsService {
    ResponseEntity<Object> create(EndpointHitDto endpointHitDto);

    ResponseEntity<Object> getViews(String rangeStart,
                                    String rangeEnd,
                                    List<String> uris,
                                    Boolean unique);
}
