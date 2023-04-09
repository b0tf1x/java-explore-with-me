package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, CommentDto commentDto);

    List<CommentDto> findByCreator(Long userId);

    CommentDto findCommentInformation(Long userId, Long commentId);

    List<CommentDto> findByEvent(Long userId, Long eventId);

    void deleteByUser(Long userId, Long commentId);

    CommentDto update(Long userId, Long commentId, Long eventId, CommentDto commentDto);
}
