package ru.practicum.ewm.events.mapper;

import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.functions.EventStatuses;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(EventFullDto eventFullDto, User user, Category category) {
        return Event.builder()
                .id(eventFullDto.getId())
                .category(category)
                .eventStatuses(EventStatuses.PENDING)
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .initiator(user)
                .annotation(eventFullDto.getAnnotation())
                .description(eventFullDto.getDescription())
                .eventDate(LocalDateTime.parse(eventFullDto.getEventDate(), FORMATTER))
                .location(eventFullDto.getLocation())
                .paid(eventFullDto.getPaid())
                .participantLimit(eventFullDto.getParticipantLimit())
                .requestModeration(eventFullDto.getRequestModeration())
                .title(eventFullDto.getTitle())
                .build();
    }

    public static EventFullDto eventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .categoryDto(CategoriesMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn().toString())
                .eventDate(event.getEventDate().toString())
                .initiator(UserMapper.toUserShort(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().toString())
                .requestModeration(event.getRequestModeration())
                .state(event.getEventStatuses().name())
                .title(event.getTitle())
                .views(0L)
                .build();
    }
    public static EventShortDto toShortDto(EventFullDto eventFullDto){
        return EventShortDto.builder()
                .id(eventFullDto.getId())
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategoryDto())
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .eventDate(eventFullDto.getEventDate())
                .initiator(eventFullDto.getInitiator())
                .title(eventFullDto.getTitle())
                .views(eventFullDto.getViews())
                .build();
    }
    public static EventShortDto toShortFromModel(Event event){
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoriesMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().toString())
                .initiator(UserMapper.toUserShort(event.getInitiator()))
                .title(event.getTitle())
                .build();
    }
}
