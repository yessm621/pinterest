package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.domain.SearchType;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.request.ArticleRequest;
import com.pinterest.dto.response.ArticleResponse;
import com.pinterest.service.ArticleService;
import com.pinterest.service.BoardService;
import com.pinterest.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final BoardService boardService;
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
        model.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("{articleId}")
    public String articlesDetail(@PathVariable Long articleId, Model model) {
        ArticleWithCommentDto article = articleService.getArticleWithComment(articleId);
        model.addAttribute("article", article);
        model.addAttribute("comments", article.getCommentDtoList());
        return "articles/detail";
    }

    @GetMapping("/form")
    public String articleForm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              Model model) {
        List<BoardDto> boards = boardService.getBoards(customUserDetails.getUsername());
        model.addAttribute("boards", boards);
        return "articles/form";
    }

    @PostMapping("/form")
    public String articleForm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              ArticleRequest articleRequest) {
        articleService.saveArticle(articleRequest.toDto(customUserDetails.toDto()));
        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String articleUpdateForm(@PathVariable Long articleId, Model model) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));
        model.addAttribute("article", article);
        return "articles/updateForm";
    }

    @PostMapping("/{articleId}/form")
    public String articlesUpdate(@PathVariable Long articleId,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                 ArticleRequest articleRequest) {
        articleService.updateArticle(articleId, articleRequest.toDto(customUserDetails.toDto()));
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String articleDelete(@PathVariable Long articleId,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        articleService.deleteArticle(articleId, customUserDetails.getUsername());
        return "redirect:/articles";
    }
}
