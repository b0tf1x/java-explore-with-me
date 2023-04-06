package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class CompilationsController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.create(updateCompilationRequest);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@Positive @PathVariable Long compId, @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "20") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return compilationService.findAll(pinned, pageable);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findById(@PathVariable Long compId) {
        return compilationService.findById(compId);
    }
}
