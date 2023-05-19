package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @GetMapping
    public String boards(Model model) {
        model.addAttribute("boards", List.of());
        return "boards/index";
    }

    @GetMapping("/create")
    public String create() {
        return "boards/create";
    }

    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId, Model model) {
        model.addAttribute("board", "board");
        model.addAttribute("articles", List.of());
        return "boards/detail";
    }
}
