package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.request.ArticleLikeRequest;
import com.pinterest.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/articleLikes")
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @PostMapping("/save")
    public String saveArticleLike(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  ArticleLikeRequest request) {
        articleLikeService.saveArticleLike(request.toDto(customUserDetails.toDto()));
        return "redirect:/articles/" + request.getArticleId();
    }

    @PostMapping("/{articleLikeId}/delete")
    public String deleteArticleLike(@PathVariable Long articleLikeId,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    ArticleLikeRequest request) {
        articleLikeService.deleteArticleLike(articleLikeId);
        return "redirect:/articles/" + request.getArticleId();
    }
}
