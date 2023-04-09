package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/users/{userId}/comments")
public class CommentsController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Positive @PathVariable Long userId,
                             @Positive @RequestParam Long eventId,
                             @Valid @RequestBody CommentDto commentDto) {
        return commentService.create(userId, eventId, commentDto);
    }

    @GetMapping
    public List<CommentDto> findByCreator(@Positive @PathVariable Long userId) {
        return commentService.findByCreator(userId);
    }

    @GetMapping("/{commentId}")
    public CommentDto findCommentInformation(@Positive @PathVariable Long userId,
                                             @Positive @PathVariable Long commentId) {
        return commentService.findCommentInformation(userId, commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> findByEvent(@Positive @PathVariable Long userId,
                                        @Positive @PathVariable Long eventId) {
        return commentService.findByEvent(userId, eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUser(@Positive @PathVariable Long userId,
                             @Positive @PathVariable Long commentId) {
        commentService.deleteByUser(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@Positive @PathVariable Long userId,
                             @Positive @PathVariable Long commentId,
                             @Positive @RequestParam Long eventId,
                             @Valid @RequestBody CommentDto commentDto) {
        return commentService.update(userId, commentId, eventId, commentDto);
    }

}
