package ru.practicum.ewm.compilations.dto;

import java.util.Set;

public class UpdateCompilationRequest {
    Set<Integer> events;
    boolean pinned;
    String title;
}
