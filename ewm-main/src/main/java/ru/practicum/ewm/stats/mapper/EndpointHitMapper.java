package ru.practicum.ewm.stats.mapper;

import ru.practicum.ewm.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(String title, HttpServletRequest httpServletRequest) {
        return new EndpointHitDto(null,
                title,
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteUser(),
                LocalDateTime.now());
    }
}
