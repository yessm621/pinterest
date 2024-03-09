package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.*;
import com.pinterest.dto.request.ArticleRequest;
import com.pinterest.service.ArticleLikeService;
import com.pinterest.service.ArticleService;
import com.pinterest.service.BoardService;
import com.pinterest.service.PaginationService;
import com.pinterest.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@Import(FormDataEncoder.class)
@DisplayName("View 컨트롤러 - Article")
class ArticleControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    BoardService boardService;

    @MockBean
    ArticleService articleService;

    @MockBean
    ArticleLikeService articleLikeService;

    @MockBean
    PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET Article 리스트 페이지 - 정상 호출")
    void givenNothing_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {
        // Given
        given(articleService.searchArticles(eq(null), any(Pageable.class)))
                .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("pagination"));
        then(articleService).should().searchArticles(eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET Article 리스트 페이지 - 키워드를 통해 정상 호출")
    void givenSearchKeyword_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {
        // Given
        String searchKeyword = "search keyword";
        given(articleService.searchArticles(eq(searchKeyword), any(Pageable.class)))
                .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("searchKeyword", searchKeyword)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("pagination"));
        then(articleService).should().searchArticles(eq(searchKeyword), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET Article 리스트 페이지 - 페이징, 정렬 기능")
    void givenPagingAndSortingParams_whenSearchingArticlesView_thenReturnArticlesView() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("pagination", barNumbers));
        then(articleService).should().searchArticles(null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET Article 상세 페이지 - 정상 호출")
    void givenNothing_whenRequestingArticleView_thenReturnArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleService.getArticleWithComment(articleId)).willReturn(createArticleWithCommentDto());
        given(boardService.getBoards(anyString())).willReturn(List.of(createBoardDto("title")));
        given(articleLikeService.getArticleLike(anyLong(), anyString())).willReturn(createArticleLikeDto());

        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("articleLike"));

        then(articleService).should().getArticleWithComment(articleId);
        then(boardService).should().getBoards(anyString());
        then(articleLikeService).should().getArticleLike(anyLong(), anyString());
    }

    @Test
    @DisplayName("[View] GET Article 상세 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestArticleView_thenRedirectsToLoginPage() throws Exception {
        // Given
        Long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("[View] GET Article 작성 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestCreateArticleView_thenRedirectsToLoginPage() throws Exception {
        mvc.perform(get("/articles/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST Article 등록 - 정상 호출")
    void givenNewArticleInfo_whenRequestCreateArticle_thenSaveNewArticle() throws Exception {
        // Given
        Long boardId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of(boardId, "title", "content", "hashtag");
        willDoNothing().given(articleService).saveArticle(any(), any(ArticleDto.class));

        // When & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(), any(ArticleDto.class));
    }

    @Test
    @DisplayName("[View] GET Article 수정 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestUpdateArticleView_thenRedirectsToLoginPage() throws Exception {
        Long articleId = 1L;
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST Article 삭제 - 정상 호출, 인증된 사용자")
    void givenArticleId_whenRequestDeleteArticle_thenDeletesArticle() throws Exception {
        long articleId = 1L;
        String email = "test@gmail.com";
        willDoNothing().given(articleService).deleteArticle(articleId, email);

        mvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().deleteArticle(articleId, email);
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

    private ArticleWithCommentDto createArticleWithCommentDto() {
        return ArticleWithCommentDto.of(
                1L,
                1L,
                createMemberDto(),
                new ArrayList<>(Arrays.asList(createCommentDto())),
                "article 내용입니다.",
                null,
                "#hashtag",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private CommentDto createCommentDto() {
        return CommentDto.of(
                1L,
                1L,
                createMemberDto(),
                "content",
                LocalDateTime.now(),
                LocalDateTime.now()
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

    private BoardDto createBoardDto(String title) {
        return BoardDto.of(
                1L,
                createMemberDto(),
                title,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private ArticleLikeDto createArticleLikeDto() {
        return ArticleLikeDto.of(
                createMemberDto(),
                1L,
                1L
        );
    }
}
