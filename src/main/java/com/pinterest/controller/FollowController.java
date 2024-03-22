package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.request.FollowRequest;
import com.pinterest.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/create")
    public String createFollow(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               FollowRequest request) {
        followService.createFollow(customUserDetails.getUsername(), request.getToMemberId());
        if (request.getType().equals("articles")) {
            return "redirect:/articles/" + request.getArticleId();
        } else {
            return "redirect:/profiles/" + request.getEmail();
        }
    }

    @PostMapping("/cancel/{followId}")
    public String cancelFollow(@PathVariable Long followId,
                               @AuthenticationPrincipal CustomUserDetails customUserDetails,
                               FollowRequest request) {
        followService.cancel(followId, customUserDetails.getUsername());
        if (request.getType().equals("articles")) {
            return "redirect:/articles/" + request.getArticleId();
        } else {
            return "redirect:/profiles/" + request.getEmail();
        }
    }

    @GetMapping("/followerList")
    @ResponseBody
    public List<MemberDto> getFollowers(String email) {
        return followService.getFollowers(email);
    }

    @GetMapping("/followingList")
    @ResponseBody
    public List<MemberDto> getFollowings(String email) {
        return followService.getFollowings(email);
    }
}
