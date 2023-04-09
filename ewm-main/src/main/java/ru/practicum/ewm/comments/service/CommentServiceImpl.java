package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentsRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceImpl implements CommentService {
    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto create(Long userId, Long eventId, CommentDto commentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentsRepository.save(CommentMapper.toComment(commentDto, user, event));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findByCreator(Long userId) {
        checkUser(userId);
        return commentsRepository.findByCreator(userId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto findCommentInformation(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException("Комментарий не найден");
        });
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findByEvent(Long userId, Long eventId) {
        checkUser(userId);
        checkEvent(eventId);
        return commentsRepository.findByEvent(eventId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUser(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = checkComment(commentId);
        if (comment.getCreator().getId() != userId) {
            throw new ConflictException("Нельзя удалить чужой комментарий");
        }
        commentsRepository.deleteById(commentId);
    }

    @Override
    public CommentDto update(Long userId, Long commentId, Long eventId, CommentDto commentDto) {
        checkUser(userId);
        checkEvent(eventId);
        Comment comment = checkComment(commentId);
        if (comment.getCreator().getId() != userId) {
            throw new ConflictException("Нельзя изменить чужой комментарий");
        }
        if (commentDto.getDescription() != null) {
            comment.setDescription(commentDto.getDescription());
        }
        comment.setCreated(LocalDateTime.now());
        comment = commentsRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private Comment checkComment(Long commentId) {
        return commentsRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException("Комментарий не найден");
        });
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не найдено");
        });
    }
}
