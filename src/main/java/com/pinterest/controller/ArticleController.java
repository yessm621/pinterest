package com.pinterest.controller;

import com.pinterest.domain.SearchType;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.service.ArticleService;
import com.pinterest.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(@RequestParam(required = false) SearchType searchType,
                           @RequestParam(required = false) String searchKeyword,
                           @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                           Model model) {
        Page<ArticleDto> articles = articleService.searchArticles(searchType, searchKeyword, pageable);
        List<Integer> pagination = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        model.addAttribute("articles", articles);
        model.addAttribute("pagination", pagination);
        return "articles/index";
    }

    @GetMapping("{articleId}")
    public String articlesDetail(@PathVariable Long articleId, Model model) {
        ArticleWithCommentDto article = articleService.getArticle(articleId);
        model.addAttribute("article", article);
        model.addAttribute("comments", article.getCommentDtoList());
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
