package ru.practicum.ewm.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comments.model.Comment;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    @Query("select comment from Comment comment " +
            "where comment.creator.id = ?1 ")
    List<Comment> findByCreator(Long userId);

    @Query("select comment from Comment comment " +
            "where comment.event.id = ?1 ")
    List<Comment> findByEvent(Long eventId);
}
