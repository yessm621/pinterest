package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
public class MemberController {

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
        model.addAttribute("profile", "profile");
        return "profile/index";
    }

    @GetMapping("/profile/create")
    public String profileCreate() {
        return "profile/create";
    }

    @GetMapping("/profile/update/{profileId}")
    public String profileModify(@PathVariable Long profileId) {
        return "profile/update";
    }
}
