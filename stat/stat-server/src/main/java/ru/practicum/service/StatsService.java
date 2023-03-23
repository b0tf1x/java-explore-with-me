package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto create(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getViewStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris,
                                Boolean unique);
}
