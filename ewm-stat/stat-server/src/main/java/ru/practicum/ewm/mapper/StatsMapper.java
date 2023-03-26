package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.ViewStats;

public class StatsMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp());
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp());
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(),
                viewStats.getUri(),
                viewStats.getHits());
    }
}
