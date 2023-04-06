package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {
    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}
