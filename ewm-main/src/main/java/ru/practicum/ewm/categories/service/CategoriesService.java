package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoriesService {
    CategoryDto create(CategoryDto categoryDto);
    void delete(long catId);
    CategoryDto put(long catId, CategoryDto categoryDto);
    List<CategoryDto> findAll(Pageable pageable);
    CategoryDto findById(Long catId);
}
