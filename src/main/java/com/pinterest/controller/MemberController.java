package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ProfileDto;
import com.pinterest.dto.request.JoinRequest;
import com.pinterest.dto.request.MemberRequest;
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

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                          Model model) {
        ProfileDto profile = memberService.getMemberEmail(customUserDetails.getUsername());
        List<ArticleDto> articles = articleService.getArticles(customUserDetails.getUsername());
        model.addAttribute("profile", profile);
        model.addAttribute("articles", articles);
        return "profile/index";
    }

    @GetMapping("/{memberId}")
    public String profile(@PathVariable Long memberId, Model model) {
        ProfileDto profile = memberService.getMember(memberId);
        List<ArticleDto> articles = articleService.getArticles(memberId);
        model.addAttribute("profile", profile);
        model.addAttribute("articles", articles);
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
        return "redirect:/members/" + profileId;
    }
}
