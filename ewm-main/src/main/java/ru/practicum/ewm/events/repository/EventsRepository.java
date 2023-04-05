package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {
    @Query("select event from Event event " +
            "where event.id in ?1 ")
    List<Event> findByEventsIds(List<Long> ids);

    @Query("select event from Event event " +
            "where event.initiator.id in ?1 " +
            "and event.eventStatuses in ?2 " +
            "and event.category in ?3 " +
            "and event.eventDate > ?4 " +
            "and event.eventDate < ?5 ")
    List<Event> findEventsAdmin(List<Long> users, List<EventStatuses> states, List<Long> categories,
                                String rangeStart, String rangeEnd);

    @Query("SELECT event " +
            "FROM Event event " +
            "WHERE " +
            "(?1 IS NULL " +
            "OR LOWER(event.description) LIKE CONCAT('%', ?1, '%') " +
            "OR LOWER(event.annotation) LIKE CONCAT('%', ?1, '%'))" +
            "AND (?2 IS NULL OR event.eventStatuses IN (?2)) " +
            "AND (?3 IS NULL OR event.category.id IN (?3)) " +
            "AND (?4 IS NULL OR event.paid = ?4) " +
            "AND (CAST(?5 AS date) IS NULL OR event.eventDate >= ?5) " +
            "AND (CAST(?6 AS date) IS NULL OR event.eventDate <= ?6) " +
            "order by event.eventDate")
    List<Event> findByParams(String text, List<EventStatuses> states,
                             List<Long> categories, Boolean paid,
                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                             Pageable pageable);

    @Query("select event from Event event " +
            "where event.initiator.id = ?1")
    List<Event> findByInitiator(Long userId);
}
