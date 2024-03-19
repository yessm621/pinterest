package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.ArticleWithFileDto;
import com.pinterest.dto.FollowDto;
import com.pinterest.dto.ProfileDto;
import com.pinterest.dto.request.MemberRequest;
import com.pinterest.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfilesController {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final FollowService followService;

    @GetMapping({"/{email}", "/{email}/{type}"})
    public String profile(@PathVariable String email,
                          @PathVariable(required = false) String type,
                          @AuthenticationPrincipal CustomUserDetails customUserDetails,
                          Model model) {
        if (type == null) type = "saved";

        ProfileDto profile = memberService.getMemberEmail(email);
        List<ArticleWithFileDto> articles;
        if (type.equals("saved")) {
            articles = articleLikeService.getArticleLikes(email);
        } else {
            articles = articleService.getArticles(email);
        }

        FollowDto follow = null;
        if (!email.equals(customUserDetails.getUsername())) {
            follow = followService.getFollow(customUserDetails.getUsername(), profile.getId());
        }
        Long countToMember = followService.countToMember(profile.getId());
        Long countFromMember = followService.countFromMember(profile.getId());
        model.addAttribute("profile", profile);
        model.addAttribute("articles", articles);
        model.addAttribute("type", type);
        model.addAttribute("follow", follow);
        model.addAttribute("countToMember", countToMember);
        model.addAttribute("countFromMember", countFromMember);
        return "profiles/index";
    }

    @GetMapping("/{profileId}/form")
    public String profileModify(@PathVariable Long profileId, Model model) {
        ProfileDto profile = memberService.getMember(profileId);
        model.addAttribute("profile", profile);
        return "profiles/updateForm";
    }

    @PostMapping("/{profileId}/form")
    public String profileModify(@PathVariable Long profileId,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                MemberRequest memberRequest) {
        profileService.updateMember(profileId, memberRequest.toDto(customUserDetails.getUsername()), file);
        return "redirect:/profiles/" + customUserDetails.getUsername();
    }
}
