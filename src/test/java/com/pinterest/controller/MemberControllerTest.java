package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.request.MemberRequest;
import com.pinterest.dto.response.MemberResponse;
import com.pinterest.service.ArticleService;
import com.pinterest.service.MemberService;
import com.pinterest.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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

@WebMvcTest(MemberController.class)
@Import(FormDataEncoder.class)
@DisplayName("View 컨트롤러 - 회원")
class MemberControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    MemberService memberService;

    @MockBean
    ArticleService articleService;

    public MemberControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @DisplayName("[View] GET 프로필 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestProfileView_thenRedirectsToLoginPage() throws Exception {
        Long memberId = 1L;
        mvc.perform(get("/members/" + memberId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(memberService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 프로필 페이지 - 정상 호출")
    void givenNoting_whenRequestingProfileView_thenReturnProfileView() throws Exception {
        // Given
        given(memberService.getMember(anyLong())).willReturn(createMemberDto());
        given(articleService.getArticles(anyString())).willReturn(List.of(createArticleDto()));

        // When & Then
        mvc.perform(get("/members/" + anyLong()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("articles"));
        then(memberService).should().getMember(anyLong());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 프로필 수정 페이지 - 정상 호출, 인증된 사용자")
    void givenNothing_whenRequestUpdateProfileView_thenReturnUpdatedProfilePage() throws Exception {
        Long memberId = 1L;
        MemberDto dto = createMemberDto();
        given(memberService.getMember(memberId)).willReturn(dto);

        mvc.perform(get("/members/" + memberId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("profile/updateForm"))
                .andExpect(model().attribute("profile", MemberResponse.from(dto)));
        then(memberService).should().getMember(memberId);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST 프로필 수정 - 정상 호출")
    void givenUpdatedMemberInfo_whenRequestUpdateMember_thenUpdatesMember() throws Exception {
        Long memberId = 1L;
        MemberRequest memberRequest = MemberRequest.of("nickname", "image");
        willDoNothing().given(memberService).updateMember(eq(memberId), any(MemberDto.class));

        mvc.perform(
                        post("/members/" + memberId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(memberRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/members/" + memberId))
                .andExpect(redirectedUrl("/members/" + memberId));
        then(memberService).should().updateMember(eq(memberId), any(MemberDto.class));
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                1L,
                1L,
                createMemberDto(),
                "title",
                "content",
                "hashtag"
        );
    }
}
