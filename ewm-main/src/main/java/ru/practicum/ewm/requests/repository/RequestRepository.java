package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.requests.dto.RequestStatuses;
import ru.practicum.ewm.requests.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select request from Request request " +
            "where request.requester.id = ?1 ")
    List<Request> findByRequesterId(Long userId);

    @Query("select request from Request request " +
            "where request.event.id = ?1 " +
            "and request.status = 'CONFIRMED'")
    List<Request> findConfirmedRequests(Long eventId);

    @Query("select request from Request request " +
            "where request.event.id in ?1 " +
            "and request.status = 'CONFIRMED'")
    List<Request> findConfirmedRequestsByIds(List<Long> eventId);

    @Query("select request from Request request " +
            "where request.event.id = ?2 " +
            "and request.event.initiator.id = ?1 ")
    List<Request> findByInitiatorAndEvent(Long userId, Long eventId);

    @Query("select request from Request request " +
            "where request.id = ?2 " +
            "and request.requester.id = ?1 ")
    Optional<Request> findByRequesterAndRequest(Long userId, Long requestId);

    @Query("select request from Request request " +
            "where request.event.id = ?1 " +
            "and request.requester.id = ?2")
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);

    @Query("select request from Request request " +
            "where request.event.id = ?1 " +
            "and request.status = ?2 ")
    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatuses status);

    @Query("select request from Request request " +
            "where request.event.id = ?1 " +
            "and request.id in ?2")
    List<Request> findByEventIdAndRequestsIds(Long eventId, List<Long> ids);
}
