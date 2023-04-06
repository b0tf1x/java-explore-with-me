package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = categoriesRepository.save(CategoriesMapper.toCategory(categoryDto));
        return CategoriesMapper.toCategoryDto(category);
    }

    @Override
    public void delete(Long catId) {
        categoriesRepository.findById(catId).orElseThrow(() -> {
            throw new NotFoundException("Категория не найдена");
        });
        categoriesRepository.deleteById(catId);
    }

    @Override
    public CategoryDto put(Long catId, CategoryDto categoryDto) {
        Category category = categoriesRepository.findById(catId).orElseThrow(() -> {
            throw new NotFoundException("Category not found");
        });
        if (categoryDto.getName().equals(category.getName())) {
            throw new ConflictException("Same category name");
        }
        category.setName(categoryDto.getName());
        return CategoriesMapper.toCategoryDto(categoriesRepository.save(category));
    }

    public List<CategoryDto> findAll(Pageable pageable) {
        return categoriesRepository.findAll(pageable).stream()
                .map(CategoriesMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto findById(Long catId) {
        Category category = categoriesRepository.findById(catId).orElseThrow(() -> {
            throw new NotFoundException("Категория не найдена");
        });
        return CategoriesMapper.toCategoryDto(category);
    }
}
