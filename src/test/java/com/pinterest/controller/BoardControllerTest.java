package com.pinterest.controller;

import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@DisplayName("View 컨트롤러 - 보드")
class BoardControllerTest {

    private final MockMvc mvc;

    @MockBean
    BoardService boardService;

    public BoardControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("[View] GET 보드 리스트 페이지 - 정상 호출")
    void givenNothing_whenRequestingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given
        given(boardService.searchBoards(eq(null), any(Pageable.class))).willReturn(Page.empty());

        // When & Then
        mvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"));
        then(boardService).should().searchBoards(eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("[View] GET 보드 리스트 페이지 - 키워드를 통해 정상 호출")
    void givenSearchKeyword_whenRequestingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given
        String searchKeyword = "title";
        given(boardService.searchBoards(eq(searchKeyword), any(Pageable.class))).willReturn(Page.empty());

        // When & Then
        mvc.perform(
                        get("/boards")
                                .queryParam("searchKeyword", searchKeyword)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"));
        then(boardService).should().searchBoards(eq(searchKeyword), any(Pageable.class));
    }

    @Test
    @DisplayName("[View] GET 보드 상세 페이지 - 정상 호출")
    void givenNothing_whenRequestBoardView_thenReturnBoardView() throws Exception {
        // Given
        Long boardId = 1L;
        given(boardService.getBoard(boardId)).willReturn(createBoardWithArticleDto());

        // When & Then
        mvc.perform(get("/boards/" + boardId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/detail"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attributeExists("articles"));
        then(boardService).should().getBoard(boardId);
    }

    private BoardWithArticleDto createBoardWithArticleDto() {
        return BoardWithArticleDto.of(
                1L,
                createMemberDto(),
                new ArrayList<>(),
                "보드 타이틀",
                "http://dummyimage.com/246x205.png/dddddd/000000",
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
                "승미입니다.",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
