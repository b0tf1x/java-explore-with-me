package ru.practicum.ewm.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.categories.model.Category;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {

}
