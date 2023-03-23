package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.model.ViewStats(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from EndpointHit hit " +
            "where hit.timestamp >= ?1 " +
            "and hit.timestamp <= ?2 " +
            "and hit.uri IN (?3) " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<ViewStats> getStatsUnique(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris);

    @Query("select new ru.practicum.model.ViewStats(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHit hit " +
            "where hit.timestamp >= ?1 " +
            "and hit.timestamp <= ?2" +
            "and hit.uri IN (?3) " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc")
    List<ViewStats> getStatsNotUnique(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris);
}
