package ru.practicum.ewm.events.mapper;

import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.ewm.events.util.EventUtil.PATTERN;

public class EventMapper {

    public static Event toEventFromCreateDto(User initiator, Category category, CreateEventDto createEventDto) {
        return new Event(null,
                createEventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                createEventDto.getDescription(),
                createEventDto.getEventDate(),
                initiator,
                createEventDto.getLocation(),
                createEventDto.getPaid(),
                createEventDto.getParticipantLimit(),
                LocalDateTime.now(),
                createEventDto.getRequestModeration(),
                EventState.PENDING,
                createEventDto.getTitle());
    }

    public static FullEventDto toFullEventDto(Event event) {
        return new FullEventDto(event.getId(),
                event.getAnnotation(),
                CategoriesMapper.toCategoryDto(event.getCategory()),
                0,
                format(event.getCreatedOn()),
                event.getDescription(),
                format(event.getEventDate()),
                UserMapper.toUserShort(event.getInitiator()),
                event.getLocation(), event.getPaid(),
                event.getParticipantLimit(),
                format(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getEventState().toString(),
                event.getTitle(), 0L);
    }

    public static ShortEventDto toShortEventDto(Event event) {
        return new ShortEventDto(event.getId(),
                event.getAnnotation(),
                CategoriesMapper.toCategoryDto(event.getCategory()),
                0,
                event.getEventDate().toString(),
                UserMapper.toUserShort(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0L);

    }

    public static ShortEventDto toShortFromFull(FullEventDto fullEventDto) {
        return new ShortEventDto(fullEventDto.getId(),
                fullEventDto.getAnnotation(),
                fullEventDto.getCategory(),
                fullEventDto.getConfirmedRequests(),
                fullEventDto.getEventDate(),
                fullEventDto.getInitiator(),
                fullEventDto.getPaid(),
                fullEventDto.getTitle(),
                fullEventDto.getViews());
    }

    public static String format(LocalDateTime value) {
        return DateTimeFormatter.ofPattern(PATTERN).format(value);
    }
}

