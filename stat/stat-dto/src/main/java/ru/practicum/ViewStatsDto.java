package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}