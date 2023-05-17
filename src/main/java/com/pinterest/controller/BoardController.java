package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @GetMapping
    public String boards() {
        return "/boards/index";
    }

    @GetMapping("create")
    public String create() {
        return "/boards/create";
    }

    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId) {
        return "/boards/detail";
    }
}
