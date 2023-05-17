package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @GetMapping
    public String articles() {
        return "articles/index";
    }

    @GetMapping("{articleId}")
    public String articlesDetail(@PathVariable Long articleId) {
        return "articles/detail";
    }

    @GetMapping("/create")
    public String articlesCreate() {
        return "articles/create";
    }

    @GetMapping("/update/{articleId}")
    public String articlesUpdate(@PathVariable Long articleId) {
        return "articles/update";
    }
}
