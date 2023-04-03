package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.model.Event;

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
}
