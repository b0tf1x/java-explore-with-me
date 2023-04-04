package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.events.repository.LocationRepository;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.requests.repository.RequestRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final CategoriesRepository categoriesRepository;
    private final LocationRepository locationRepository;
    private final EntityManager entityManager;
    private final RequestRepository requestRepository;

    @Override
    public List<EventFullDto> findEventsAdmin(List<Long> users, List<EventStatuses> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> eventCriteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = eventCriteriaQuery.from(Event.class);
        List<Predicate> predicateList = new ArrayList<>();

        TypedQuery<Event> typedQuery = entityManager.createQuery(eventCriteriaQuery);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        if (users != null) {
            predicateList.add(criteriaBuilder.and(eventRoot.get("initiator").get("id").in(users)));
        }

        if (categories != null) {
            predicateList.add(criteriaBuilder.and(eventRoot.get("category").get("id").in(categories)));
        }

        if (states != null) {
            predicateList.add(criteriaBuilder.and(states.stream().map(eventState -> criteriaBuilder.equal(eventRoot.get("eventState"), eventState)).toArray(Predicate[]::new)));
        }

        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            predicateList.add(criteriaBuilder.and(criteriaBuilder.between(eventRoot.get("eventDate"), start, end)));
        }

        return entityManager.createQuery(eventCriteriaQuery.select(eventRoot).where(predicateList.toArray(Predicate[]::new))).getResultList().stream().map(EventMapper::eventFullDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
        if (event.getEventStatuses().equals(EventStatuses.PUBLISHED) && updateEventAdminRequest.getStateAction().equals(EventStatuses.PUBLISHED.toString())) {
            throw new BadRequestException("Событие уже опубликовано");
        }
        if (LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Событие уже прошло");
        }
        if (event.getEventStatuses().equals(EventStatuses.CANCELED.name()) && updateEventAdminRequest.getStateAction().equals(EventStatuses.PUBLISHED.toString())) {
            throw new BadRequestException("Нельзя опубликовать. Событие отмененено.");
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoriesRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> {
                throw new NotFoundException("Категория не найдена");
            });
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationRepository.save(updateEventAdminRequest.getLocation());
            event.setLocation(location);
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(EventStatuses.PUBLISHED.name())) {
                event.setEventStatuses(EventStatuses.PUBLISHED);
            }
        }
        event = eventsRepository.save(event);
        return EventMapper.eventFullDto(event);
    }

    @Override
    public List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable) {
        /**LocalDateTime start = null;
         LocalDateTime end = null;
         if (rangeStart != null) {
         start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         }
         if (rangeStart != null) {
         end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         }
         if (text == null) text = "";
         List<Event> events = eventsRepository.findByParamsUser(text.toLowerCase(), List.of(EventStatuses.PUBLISHED),
         categories, paid, start, end, pageable);
         List<EventFullDto> fullEventDtoList = events.stream()
         .map(EventMapper::eventFullDto)
         .collect(Collectors.toList());
         fullEventDtoList.forEach(event -> event.setConfirmedRequests(requestRepository
         .findByEventIdConfirmed(event.getId()).size()));
         if (onlyAvailable) {
         fullEventDtoList = fullEventDtoList.stream()
         .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
         .collect(Collectors.toList());
         }**/

        return new ArrayList<>();
    }

    @Override
    public EventShortDto findById(Long eventId) {
        return new EventShortDto();
    }

}
