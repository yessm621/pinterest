package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.request.CommentRequest;
import com.pinterest.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    private String createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                 CommentRequest commentRequest) {
        commentService.saveComment(commentRequest.toDto(customUserDetails.toDto()));
        return "redirect:/articles/" + commentRequest.getArticleId();
    }
}
