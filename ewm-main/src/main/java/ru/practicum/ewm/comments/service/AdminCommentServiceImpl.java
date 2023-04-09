package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentsRepository;
import ru.practicum.ewm.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentsRepository commentsRepository;

    @Override
    public void deleteByAdmin(Long commentId) {
        checkComment(commentId);
        commentsRepository.deleteById(commentId);
    }

    private Comment checkComment(Long commentId) {
        return commentsRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException("Комментарий не найден");
        });
    }

}
