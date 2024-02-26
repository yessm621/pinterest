package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.SubscribeDto;
import com.pinterest.dto.request.SubscribeRequest;
import com.pinterest.service.SubscribeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST 구독 페이지 - 구독/구독취소 하기")
    void givenSubscribeInfo_whenRequestSubscribeOrCancel_thenSave() throws Exception {
        // Given
        Long boardId = 1L;
        SubscribeRequest subscribeRequest = SubscribeRequest.of(boardId);
        willDoNothing().given(subscribeService).saveSubscribe(any(SubscribeDto.class));

        // When & Then
        mvc.perform(
                        post("/subscribes")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .contentType(formDataEncoder.encode(subscribeRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/subscribes"))
                .andExpect(redirectedUrl("/subscribes"));
        then(subscribeService).should().saveSubscribe(any(SubscribeDto.class));
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