package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.model.Location;
@Data
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
