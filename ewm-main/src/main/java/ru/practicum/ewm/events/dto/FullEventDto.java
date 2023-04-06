package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FullEventDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;
}
