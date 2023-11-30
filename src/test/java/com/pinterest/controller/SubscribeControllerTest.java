package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.service.SubscribeService;
import com.pinterest.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscribeController.class)
@Import(FormDataEncoder.class)
@DisplayName("View 컨트롤러 - 구독")
class SubscribeControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    SubscribeService subscribeService;

    public SubscribeControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 구독 페이지 - 정상호출")
    void givenNothing_whenRequestSubscribeView_thenReturnSubscribeView() throws Exception {
        // Given
        given(subscribeService.searchBoards(any())).willReturn(List.of(createBoardDto()));
        given(subscribeService.searchArticles(any())).willReturn(List.of(createArticleDto()));

        // When & Then
        mvc.perform(get("/subscribes"))
                .andExpect(status().isOk())
                .andExpect(view().name("subscribes/index"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(model().attributeExists("articles"));
        then(subscribeService).should().searchBoards(any());
        then(subscribeService).should().searchArticles(any());
    }

    @Test
    @DisplayName("[View] GET 구독 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestSubscribeView_thenRedirectsToLoginPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/subscribes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(subscribeService).shouldHaveNoInteractions();
    }

    private BoardDto createBoardDto() {
        return BoardDto.of(
                createMemberDto(),
                "title",
                "image"
        );
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "승미입니다.",
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
                "image",
                "hashtag"
        );
    }
}