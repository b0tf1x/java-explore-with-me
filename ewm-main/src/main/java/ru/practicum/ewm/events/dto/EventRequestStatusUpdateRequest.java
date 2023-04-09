package ru.practicum.ewm.events.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotNull
    List<Long> requestIds;

    @NotNull
    String status;
}
