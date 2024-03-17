package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.*;
import com.pinterest.dto.request.ArticleRequest;
import com.pinterest.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final MemberService memberService;
    private final BoardService boardService;
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final FollowService followService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(@RequestParam(required = false) String searchKeyword,
                           @PageableDefault(size = 30) Pageable pageable,
                           Model model) {
        Page<ArticleDto> articles = articleService.searchArticles(searchKeyword, pageable);
        List<Integer> pagination = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        model.addAttribute("articles", articles);
        model.addAttribute("pagination", pagination);

        return "articles/index";
    }

    @GetMapping("{articleId}")
    public String articlesDetail(@PathVariable Long articleId,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                 Model model) {
        ProfileDto profile = memberService.getMemberEmail(customUserDetails.getUsername());
        List<BoardDto> boards = boardService.getBoards(customUserDetails.getUsername());
        ArticleWithCommentDto article = articleService.getArticleWithComment(articleId);
        ArticleLikeDto articleLike = articleLikeService.getArticleLike(articleId, customUserDetails.getUsername());
        FollowDto follow = followService.getFollow(customUserDetails.getUsername(), article.getMemberDto().getId());
        Long countToMember = followService.countToMember(article.getMemberDto().getId());
        model.addAttribute("profile", profile);
        model.addAttribute("boards", boards);
        model.addAttribute("article", article);
        model.addAttribute("comments", article.getCommentDtoList());
        model.addAttribute("articleLike", articleLike);
        model.addAttribute("follow", follow);
        model.addAttribute("countToMember", countToMember);
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
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              ArticleRequest articleRequest) {
        articleService.saveArticle(file, articleRequest.toDto(customUserDetails.toDto()));
        return "redirect:/articles";
    }

    @PostMapping("/{articleId}/delete")
    public String articleDelete(@PathVariable Long articleId,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        articleService.deleteArticle(articleId, customUserDetails.getUsername());
        return "redirect:/articles";
    }
}
