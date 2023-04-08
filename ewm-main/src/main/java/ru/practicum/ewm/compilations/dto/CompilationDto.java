package ru.practicum.ewm.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.events.dto.ShortEventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private List<ShortEventDto> events;
    private Boolean pinned;
    private String title;
}
