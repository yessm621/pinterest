package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.request.SubscribeRequest;
import com.pinterest.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("subscribes")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @GetMapping
    public String subscribe(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        List<BoardDto> boardList = subscribeService.searchBoards(customUserDetails.getUsername());
        List<ArticleDto> articleList = subscribeService.searchArticles(customUserDetails.getUsername());
        model.addAttribute("boards", boardList);
        model.addAttribute("articles", articleList);
        return "subscribes/index";
    }

    @PostMapping
    public String saveSubscribe(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                SubscribeRequest request) {
        subscribeService.saveSubscribe(request.toDto(customUserDetails.toDto()));
        return "redirect:/boards/" + request.getBoardId();
    }
}
