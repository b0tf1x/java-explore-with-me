package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.events.model.Location;

public class UpdateEventUserRequest {
    String annotation;
    int category;
    String description;
    String eventDate;
    Location location;
    boolean paid;
    int participantLimit;
    boolean requestModeration;
    String stateAction;
    String title;
}
