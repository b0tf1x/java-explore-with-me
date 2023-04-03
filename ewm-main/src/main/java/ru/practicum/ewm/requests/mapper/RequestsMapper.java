package ru.practicum.ewm.requests.mapper;

import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

public class RequestsMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }
}
