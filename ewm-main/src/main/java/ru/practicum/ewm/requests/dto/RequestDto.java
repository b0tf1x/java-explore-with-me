package ru.practicum.ewm.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static ru.practicum.ewm.events.util.EventUtil.PATTERN;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    @JsonFormat(pattern = PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatuses status;
}
