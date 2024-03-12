package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ProfileDto;
import com.pinterest.dto.request.JoinRequest;
import com.pinterest.dto.request.MemberRequest;
import com.pinterest.service.ArticleLikeService;
import com.pinterest.service.ArticleService;
import com.pinterest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    @GetMapping("/login")
    public String login() {
        return "members/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "members/signup";
    }

    @PostMapping("/signup")
    public String signup(JoinRequest request) {
        memberService.save(request);
        return "redirect:/members/login";
    }

    @GetMapping({"/{email}", "/{email}/{type}"})
    public String profile(@PathVariable String email,
                          @PathVariable(required = false) String type,
                          @AuthenticationPrincipal CustomUserDetails customUserDetails,
                          Model model) {
        if (type == null) type = "saved";

        ProfileDto profile = memberService.getMemberEmail(email);
        List<ArticleDto> articles;
        if (type.equals("created")) {
            articles = articleService.getArticles(email);
        } else {
            articles = articleLikeService.getArticleLikes(email);
        }
        model.addAttribute("profile", profile);
        model.addAttribute("articles", articles);
        model.addAttribute("type", type);
        return "profile/index";
    }

    @GetMapping("/{profileId}/form")
    public String profileModify(@PathVariable Long profileId, Model model) {
        ProfileDto profile = memberService.getMember(profileId);
        model.addAttribute("profile", profile);
        return "profile/updateForm";
    }

    @PostMapping("/{profileId}/form")
    public String profileModify(@PathVariable Long profileId,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                MemberRequest memberRequest) {
        memberService.updateMember(profileId, memberRequest.toDto(customUserDetails.getUsername()), file);
        return "redirect:/members/" + customUserDetails.getUsername();
    }
}
