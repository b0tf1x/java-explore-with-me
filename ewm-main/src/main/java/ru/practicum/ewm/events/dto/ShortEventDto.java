package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShortEventDto extends EventDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
