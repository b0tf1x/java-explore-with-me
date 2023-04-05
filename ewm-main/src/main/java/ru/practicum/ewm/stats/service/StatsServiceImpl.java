package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsClient statsClient;

    @Transactional
    @Override
    public ResponseEntity<Object> create(EndpointHitDto endpointHitDto) {
        return statsClient.create(endpointHitDto);
    }

    @Override
    public ResponseEntity<Object> getViews(String rangeStart,
                                           String rangeEnd,
                                           List<String> uris,
                                           Boolean unique) {
        return statsClient.getViewStats(rangeStart, rangeEnd, uris, unique);
    }

}
