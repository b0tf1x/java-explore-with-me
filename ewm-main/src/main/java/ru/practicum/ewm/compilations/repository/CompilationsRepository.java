package ru.practicum.ewm.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.compilations.model.Compilation;

import java.util.List;

public interface CompilationsRepository extends JpaRepository<Compilation, Long> {
    @Query("select compilation from Compilation compilation " +
            "where compilation.pinned = ?1")
    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
