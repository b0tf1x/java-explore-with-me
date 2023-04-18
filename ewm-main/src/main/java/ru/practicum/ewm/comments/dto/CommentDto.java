package ru.practicum.ewm.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.ewm.events.util.EventUtil.PATTERN;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    @NotNull
    private String description;
    @JsonFormat(pattern = PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;
    private Long eventId;
    private Long creatorId;
}
