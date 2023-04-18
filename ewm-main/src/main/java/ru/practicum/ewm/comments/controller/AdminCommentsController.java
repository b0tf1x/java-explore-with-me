package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comments.service.AdminCommentService;

import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/admin/users/comment")
public class AdminCommentsController {
    private final AdminCommentService adminCommentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@Positive @PathVariable Long commentId) {
        adminCommentService.deleteByAdmin(commentId);
    }
}
