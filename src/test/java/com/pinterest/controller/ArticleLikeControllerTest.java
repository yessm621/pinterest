package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.ArticleLikeDto;
import com.pinterest.dto.request.ArticleLikeRequest;
import com.pinterest.service.ArticleLikeService;
import com.pinterest.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleLikeController.class)
@Import(FormDataEncoder.class)
@DisplayName("View 컨트롤러 - ArticleLike")
class ArticleLikeControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    ArticleLikeService articleLikeService;

    public ArticleLikeControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @WithMockCustomUser
    @DisplayName("POST - ArticleLike 저장")
    void givenArticleLikeRequest_whenRequestCreateArticleLike_thenSaveArticleLike() throws Exception {
        Long boardId = 1L;
        Long articleId = 1L;
        ArticleLikeRequest request = new ArticleLikeRequest(boardId, articleId);
        willDoNothing().given(articleLikeService).saveArticleLike(any(ArticleLikeDto.class));

        mvc.perform(
                        post("/articleLikes/save")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(articleLikeService).should().saveArticleLike(any(ArticleLikeDto.class));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("POST - ArticleLike 저장 취소")
    void givenArticleLikeId_whenRequestDeleteArticleLike_thenDeleteArticleLike() throws Exception {
        Long articleLikeId = 1L;
        Long boardId = 1L;
        Long articleId = 1L;
        ArticleLikeRequest request = new ArticleLikeRequest(boardId, articleId);
        willDoNothing().given(articleLikeService).deleteArticleLike(articleLikeId);

        mvc.perform(
                        post("/articleLikes/" + articleLikeId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(articleLikeService).should().deleteArticleLike(articleLikeId);
    }
}