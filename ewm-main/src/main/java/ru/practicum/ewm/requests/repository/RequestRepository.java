package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.requests.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select request from Request request " +
            "where request.requester.id = ?1 ")
    List<Request> findByRequesterId(Long userId);

    @Query("select request from Request request " +
            "where request.event.id = ?1 " +
            "and request.status = 'CONFIRMED'")
    List<Request> findConfirmedRequests(Long eventId);

    @Query("select request from Request request " +
            "where request.event.id = ?2 " +
            "and request.requester.id = ?1 ")
    List<Request> findByInitiatorAndEvent(Long userId, Long eventId);

    @Query("select request from Request request " +
            "where request.id = ?2 " +
            "and request.requester.id = ?1 ")
    Optional<Request> findByInitiatorAndRequest(Long userId, Long requestId);

    Optional<Request> findByEventIdAndRequesterId(Long requestId, Long userId);
}
