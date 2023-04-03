package ru.practicum.ewm.compilations.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(UpdateCompilationRequest updateCompilationRequest);
    void delete(Long compId);
    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);
    List<CompilationDto> findAll(Boolean pinned, Pageable pageable);
    CompilationDto findById(Long compId);
}
