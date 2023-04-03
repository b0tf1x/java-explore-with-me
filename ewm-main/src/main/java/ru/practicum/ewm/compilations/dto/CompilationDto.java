package ru.practicum.ewm.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
