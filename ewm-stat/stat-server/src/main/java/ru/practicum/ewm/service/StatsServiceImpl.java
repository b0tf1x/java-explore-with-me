package ru.practicum.ewm.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.repository.StatsRepository;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
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
            return Collections.emptyList();
        }
        if (unique) {
            return statsRepository.getStatsUnique(start, end, uris).stream()
                    .map(StatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        } else {
            return statsRepository.getStatsNotUnique(start, end, uris).stream()
                    .map(StatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        }
    }
}
