package ru.practicum.ewm.events.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    protected Long id;
    private Long views;
    private Integer confirmedRequests;
}
