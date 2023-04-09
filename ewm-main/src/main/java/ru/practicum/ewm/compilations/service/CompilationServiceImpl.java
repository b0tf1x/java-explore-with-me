package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationsRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventsRepository;

    @Override
    public CompilationDto create(UpdateCompilationRequest updateCompilationRequest) {
        List<Event> events = eventsRepository.findByIds(updateCompilationRequest.getEvents());
        Compilation compilation = compilationsRepository.save(CompilationMapper.toCompilation(updateCompilationRequest, events));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void delete(Long compId) {
        compilationsRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException("Compilation not found");
        });
        compilationsRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationsRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException("Compilation not found");
        });
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventsRepository.findByIds(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilation = compilationsRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Pageable pageable) {
        return compilationsRepository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationsRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException("Compilation not found");
        });
        return CompilationMapper.toCompilationDto(compilation);
    }
}
