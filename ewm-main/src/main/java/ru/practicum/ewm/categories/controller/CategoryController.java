package ru.practicum.ewm.categories.controller;

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
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.service.CategoriesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class CategoryController {
    private final CategoriesService categoriesService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        return categoriesService.create(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long catId) {
        categoriesService.delete(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto put(@Positive @PathVariable Long catId, @Valid @RequestBody CategoryDto categoryDto) {
        return categoriesService.put(catId, categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findAll(
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "20") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return categoriesService.findAll(pageable);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findById(@Positive @PathVariable Long catId) {
        return categoriesService.findById(catId);
    }
}
