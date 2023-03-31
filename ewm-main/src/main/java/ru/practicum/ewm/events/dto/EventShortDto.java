package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

public class EventShortDto {
    long id;
    String annotation;
    CategoryDto category;
    long confirmedRequests;
    String eventDate;
    UserShortDto initiator;
    String title;
    long views;
}
