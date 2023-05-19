package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @GetMapping
    public String articles(Model model) {
        model.addAttribute("articles", List.of());
        return "articles/index";
    }

    @GetMapping("{articleId}")
    public String articlesDetail(@PathVariable Long articleId, Model model) {
        model.addAttribute("article", "article");
        model.addAttribute("articleComments", List.of());
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
