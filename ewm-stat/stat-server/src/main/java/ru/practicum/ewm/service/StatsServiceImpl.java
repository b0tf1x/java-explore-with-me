package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(StatsMapper.toEndpointHit(endpointHitDto));
        return StatsMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsUniqueNotUri(start, end).stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            } else {
                return statsRepository.getStatsNotUniqueNotUri(start, end).stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            }
        } else if (unique) {
            return statsRepository.getUniqueStats(start, end, uris).stream()
                    .map(StatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        } else {
            return statsRepository.getNotUniqueStats(start, end, uris).stream()
                    .map(StatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        }
    }
}
