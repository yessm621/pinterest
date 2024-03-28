package com.pinterest.controller;

import com.pinterest.config.WithMockCustomUser;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.request.BoardCreateRequest;
import com.pinterest.dto.request.BoardUpdateRequest;
import com.pinterest.service.ArticleLikeService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@Import(FormDataEncoder.class)
@DisplayName("View 컨트롤러 - 보드")
class BoardControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    BoardService boardService;

    @MockBean
    ArticleLikeService articleLikeService;

    @MockBean
    PaginationService paginationService;

    public BoardControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 보드 리스트 페이지 - 정상 호출")
    void givenNothing_whenRequestingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given
        given(boardService.searchBoards(anyString(), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(model().attributeExists("pagination"));

        then(boardService).should().searchBoards(anyString(), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("[View] GET 리스트 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestingBoardsView_thenRedirectsToLoginPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/boards"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(boardService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 보드 상세 페이지 - 정상 호출, 인증된 사용자")
    void givenNothing_whenRequestBoardView_thenReturnBoardView() throws Exception {
        // Given
        Long boardId = 1L;
        String email = "test@gmail.com";
        given(boardService.getBoard(boardId)).willReturn(createBoardDto());
        given(articleLikeService.getArticleLikes(boardId, email)).willReturn(List.of(createArticleDto()));

        // When & Then
        mvc.perform(get("/boards/" + boardId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/detail"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attributeExists("articles"));
        then(boardService).should().getBoard(boardId);
        then(articleLikeService).should().getArticleLikes(boardId, email);
    }

    @Test
    @DisplayName("[View] GET 보드 상세 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequestBoardView_thenRedirectsToLoginPage() throws Exception {
        // Given
        Long boardId = 1L;

        // When & Then
        mvc.perform(get("/boards/" + boardId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(boardService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] GET 보드 페이지 -> 보드 생성 페이지로 이동")
    void givenNothing_whenRequestCreateBoardView_thenReturnsBoardView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/boards/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/create"));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST 보드 페이지 -> 보드 생성 페이지에서 보드를 생성")
    void givenBoardRequest_whenRequestCreateBoard_thenReturnsBoard() throws Exception {
        // Given
        BoardCreateRequest request = BoardCreateRequest.of("title", "boards");
        willDoNothing().given(boardService).saveBoard(any(BoardDto.class));

        // When & Then
        mvc.perform(
                        post("/boards/create")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/boards"))
                .andExpect(redirectedUrl("/boards"));
        then(boardService).should().saveBoard(any(BoardDto.class));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST 보드 수정 - 정상 호출")
    void givenUpdatedBoardInfo_whenRequesting_thenUpdatesNewBoard() throws Exception {
        // Given
        Long boardId = 1L;
        BoardUpdateRequest request = BoardUpdateRequest.of("title");
        willDoNothing().given(boardService).updateBoard(eq(boardId), any(BoardDto.class));

        // When & Then
        mvc.perform(
                        post("/boards/" + boardId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/boards"))
                .andExpect(redirectedUrl("/boards"));
        then(boardService).should().updateBoard(eq(boardId), any(BoardDto.class));
    }

    @Test
    @DisplayName("[View] GET 보드 수정 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequesting_thenRedirectsToLoginPage() throws Exception {
        // Given
        Long boardId = 1L;

        // When & Then
        mvc.perform(get("/boards/" + boardId + "form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(boardService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[View] POST 보드 삭제 - 정상 호출")
    void givenBoardIdToDelete_whenRequesting_thenDeletesBoard() throws Exception {
        // Given
        long boardId = 1L;
        String email = "test@gmail.com";
        willDoNothing().given(boardService).deleteBoard(boardId, email);

        // When & Then
        mvc.perform(
                        post("/boards/" + boardId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/boards"))
                .andExpect(redirectedUrl("/boards"));
        then(boardService).should().deleteBoard(boardId, email);
    }

    private BoardDto createBoardDto() {
        return BoardDto.of(
                createMemberDto(),
                "title"
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
                "hashtag",
                "image"
        );
    }
}
