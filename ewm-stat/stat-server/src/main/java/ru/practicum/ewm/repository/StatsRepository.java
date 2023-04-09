package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.model.ViewStats(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from EndpointHit hit " +
            "where (hit.timestamp between ?1 and ?2) " +
            "and hit.uri IN ?3 " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<ViewStats> getUniqueStats(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris);

    @Query("select new ru.practicum.ewm.model.ViewStats(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHit hit " +
            "where (hit.timestamp between ?1 and ?2) " +
            "and hit.uri IN ?3 " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc")
    List<ViewStats> getNotUniqueStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris);

    @Query("select new ru.practicum.ewm.model.ViewStats(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from EndpointHit hit " +
            "where (hit.timestamp between ?1 and ?2) " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<ViewStats> getStatsUniqueNotUri(LocalDateTime start,
                                         LocalDateTime end);

    @Query("select new ru.practicum.ewm.model.ViewStats(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHit hit " +
            "where (hit.timestamp between ?1 and ?2) " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc")
    List<ViewStats> getStatsNotUniqueNotUri(LocalDateTime start,
                                            LocalDateTime end);
}
