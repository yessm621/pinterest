package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.FileDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.ProfileDto;
import com.pinterest.dto.request.MemberRequest;
import com.pinterest.service.*;
import com.pinterest.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfilesController.class)
@Import(FormDataEncoder.class)
@DisplayName("View 컨트롤러 - 프로필")
class ProfilesControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    MemberService memberService;

    @MockBean
    ProfileService profileService;

    @MockBean
    ArticleService articleService;

    @MockBean
    ArticleLikeService articleLikeService;

    @MockBean
    FollowService followService;

    public ProfilesControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 프로필 페이지 - 정상 호출")
    void givenNoting_whenRequestingProfileView_thenReturnProfileView() throws Exception {
        // Given
        String email = "test@gmail.com";
        given(memberService.getMemberEmail(anyString())).willReturn(createProfileDto());
        given(articleLikeService.getArticleLikes(anyString())).willReturn(List.of(createArticleDto()));
        given(followService.countToMember(anyLong())).willReturn(100L);
        given(followService.countFromMember(anyLong())).willReturn(100L);

        // When & Then
        mvc.perform(get("/profiles/" + email))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("profiles/index"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("countToMember"))
                .andExpect(model().attributeExists("countFromMember"));

        then(memberService).should().getMemberEmail(anyString());
        then(articleLikeService).should().getArticleLikes(anyString());
        then(followService).should().countToMember(anyLong());
        then(followService).should().countFromMember(anyLong());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 프로필 수정 페이지 - 정상 호출, 인증된 사용자")
    void givenNothing_whenRequestUpdateProfileView_thenReturnUpdatedProfilePage() throws Exception {
        Long profileId = 1L;
        ProfileDto dto = createProfileDto();
        given(memberService.getMember(profileId)).willReturn(dto);

        mvc.perform(get("/profiles/" + profileId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("profiles/updateForm"))
                .andExpect(model().attribute("profile", dto));
        then(memberService).should().getMember(profileId);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST 프로필 수정 - 정상 호출")
    void givenUpdatedMemberInfo_whenRequestUpdateMember_thenUpdatesMember() throws Exception {
        Long profileId = 1L;
        String email = "test@gmail.com";
        MemberRequest request = MemberRequest.of("nickname", "image");
        willDoNothing().given(profileService).updateMember(anyLong(), any(MemberDto.class), any(MockMultipartFile.class));

        mvc.perform(
                        post("/profiles/" + profileId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profiles/" + email))
                .andExpect(redirectedUrl("/profiles/" + email));
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "test2@gmail.com",
                "test123",
                "test2",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                1L,
                createMemberDto(),
                "title",
                "content",
                "hashtag",
                "image"
        );
    }

    private ProfileDto createProfileDto() {
        return ProfileDto.of(
                1L,
                createFileDto().getSavedName(),
                "test@gmail.com",
                "test",
                "image"
        );
    }

    private FileDto createFileDto() {
        return new FileDto(
                "fileName",
                "savedName",
                "savedPath"
        );
    }
}