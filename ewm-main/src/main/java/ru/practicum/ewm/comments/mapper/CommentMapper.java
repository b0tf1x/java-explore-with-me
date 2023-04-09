package ru.practicum.ewm.comments.mapper;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.model.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getDescription(),
                comment.getCreated(),
                comment.getEvent().getId(),
                comment.getCreator().getId());
    }

    public static Comment toComment(CommentDto commentDto, User creator, Event event) {
        return new Comment(commentDto.getId(),
                commentDto.getDescription(),
                commentDto.getCreated(),
                event,
                creator);
    }
}
