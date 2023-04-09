package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.events.model.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    String annotation;
    Long category;
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    int participantLimit;
    Boolean requestModeration;
    String stateAction;
    String title;
}
