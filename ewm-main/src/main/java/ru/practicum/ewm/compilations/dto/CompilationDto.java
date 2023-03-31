package ru.practicum.ewm.compilations.dto;

import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.List;

public class CompilationDto {
    List<EventShortDto> events;
    boolean pinned;
    String title;
}
