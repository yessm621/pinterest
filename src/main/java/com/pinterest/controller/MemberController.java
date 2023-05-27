package com.pinterest.controller;

import com.pinterest.dto.MemberDto;
import com.pinterest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "members/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "members/signup";
    }

    @GetMapping("/{profileId}")
    public String profile(@PathVariable Long profileId, Model model) {
        MemberDto member = memberService.getMember(profileId);
        model.addAttribute("profile", member);
        return "profile/index";
    }

    @GetMapping("/create")
    public String profileCreate() {
        return "profile/create";
    }

    @GetMapping("/update/{profileId}")
    public String profileModify(@PathVariable Long profileId) {
        return "profile/update";
    }
}
