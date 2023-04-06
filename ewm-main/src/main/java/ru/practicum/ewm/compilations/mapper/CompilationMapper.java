package ru.practicum.ewm.compilations.mapper;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(UpdateCompilationRequest updateCompilationRequest, List<Event> events) {
        return new Compilation(null,
                updateCompilationRequest.getPinned(),
                updateCompilationRequest.getTitle(),
                events);
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                compilation.getEvents().stream()
                        .map(EventMapper::toShortEventDto)
                        .collect(Collectors.toList()),
                compilation.getPinned(),
                compilation.getTitle());
    }
}
