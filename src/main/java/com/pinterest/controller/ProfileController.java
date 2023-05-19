package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public String profile(Model model) {
        model.addAttribute("profile", "profile");
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
