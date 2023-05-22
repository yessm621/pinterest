package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("subscribes")
public class SubscribeController {

    @GetMapping
    public String subscribe(Model model) {
        model.addAttribute("boards", List.of());
        return "subscribes/index";
    }
}
