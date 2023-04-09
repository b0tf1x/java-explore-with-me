package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.requests.dto.RequestDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}
