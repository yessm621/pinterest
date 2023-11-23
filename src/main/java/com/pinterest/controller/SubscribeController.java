package com.pinterest.controller;

import com.pinterest.config.MemberPrincipal;
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
    public String subscribe(Model model) {
        model.addAttribute("boards", List.of());
        return "subscribes/index";
    }

    @PostMapping
    public String saveSubscribe(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
                                SubscribeRequest request) {
        subscribeService.saveSubscribe(request.toDto(memberPrincipal.toDto()));
        return "redirect:/articles/" + request.getArticleId();
    }
}
