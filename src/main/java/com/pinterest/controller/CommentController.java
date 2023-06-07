package com.pinterest.controller;

import com.pinterest.dto.CommentDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.request.CommentRequest;
import com.pinterest.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    private String newComment(CommentRequest commentRequest) {
        // TODO: 인증 정보를 넣어야 한다.
        CommentDto dto = commentRequest.toDto(MemberDto.of(
                1L, "yessm621@gmail.com", "test123", "yessm", "yessm621", "This is memo", null, null
        ));
        commentService.saveComment(dto);
        return "redirect:/articles/" + commentRequest.getArticleId();
    }

    @PostMapping("{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Long articleId) {
        commentService.deleteComment(commentId);
        return "redirect:/articles/" + articleId;
    }
}
