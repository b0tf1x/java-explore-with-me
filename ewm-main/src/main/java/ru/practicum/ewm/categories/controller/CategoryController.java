package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.service.CategoriesService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryController {
    private final CategoriesService categoriesService;

    @PostMapping("/admin/categories")
    public CategoryDto create(@RequestBody CategoryDto categoryDto) {
        return categoriesService.create(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void delete(@PathVariable long catId) {
        categoriesService.delete(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto put(@PathVariable long catId, @RequestBody CategoryDto categoryDto) {
        return categoriesService.put(catId, categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findAll(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return categoriesService.findAll(pageable);
    }
    @GetMapping("/categories/{catId}")
    public CategoryDto findById(@PathVariable Long catId){
        return categoriesService.findById(catId);
    }
}
