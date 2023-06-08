package com.pinterest.controller;

import com.pinterest.dto.CommentDto;
import com.pinterest.dto.request.CommentRequest;
import com.pinterest.service.CommentService;
import com.pinterest.util.FormDataEncoder;
import com.pinterest.util.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@DisplayName("View 컨트롤러 - Comment")
class CommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    CommentService commentService;

    public CommentControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("[View] POST 댓글 등록 - 정상 호출")
    void givenCommentInfo_whenRequesting_thenSavesNewComment() throws Exception {
        // Given
        long articleId = 1L;
        CommentRequest request = CommentRequest.of(articleId, "댓글");
        willDoNothing().given(commentService).saveComment(any(CommentDto.class));

        // When & Then
        mvc.perform(
                        post("/comments/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(commentService).should().saveComment(any(CommentDto.class));
    }

    @WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("[View] POST 댓글 삭제 - 정상 호출")
    void givenCommentIdTo_whenRequesting_thenDeletesComment() throws Exception {
        // Given
        long articleId = 1L;
        long commentId = 1L;
        String email = "test@gmail.com";
        willDoNothing().given(commentService).deleteComment(commentId, email);

        // When & Then
        mvc.perform(
                        post("/comments/" + commentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(commentService).should().deleteComment(commentId, email);
    }
}
