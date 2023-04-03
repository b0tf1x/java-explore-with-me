package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = categoriesRepository.save(CategoriesMapper.toCategory(categoryDto));
        return CategoriesMapper.toCategoryDto(category);
    }

    @Override
    public void delete(long catId) {
        categoriesRepository.deleteById(catId);
    }

    @Override
    public CategoryDto put(long catId, CategoryDto categoryDto) {
        Category category = categoriesRepository.findById(catId).orElseThrow(() -> {
            throw new NotFoundException("Категория не найдена");
        });
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        categoriesRepository.save(category);
        return CategoriesMapper.toCategoryDto(category);
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
