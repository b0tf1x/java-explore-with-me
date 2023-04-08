package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {
    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}
