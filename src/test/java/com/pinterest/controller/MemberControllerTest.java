package com.pinterest.controller;

import com.pinterest.service.ArticleLikeService;
import com.pinterest.service.ArticleService;
import com.pinterest.service.MemberService;
import com.pinterest.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @MockBean
    ArticleLikeService articleLikeService;

    public MemberControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @DisplayName("[View] GET 프로필 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestProfileView_thenRedirectsToLoginPage() throws Exception {
        String email = "test@gmail.com";
        mvc.perform(get("/members/" + email))
                .andExpect(status().is3xxRedirection());
        then(memberService).shouldHaveNoInteractions();
    }
}
