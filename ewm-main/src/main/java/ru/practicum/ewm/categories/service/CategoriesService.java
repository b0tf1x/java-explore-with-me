package ru.practicum.ewm.categories.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.categories.dto.CategoryDto;

import java.util.List;

public interface CategoriesService {
    CategoryDto create(CategoryDto categoryDto);

    void delete(Long catId);

    CategoryDto put(Long catId, CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long catId);
}
