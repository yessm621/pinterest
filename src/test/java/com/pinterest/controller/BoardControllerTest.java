package com.pinterest.controller;

import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.service.BoardService;
import com.pinterest.service.PaginationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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

    @MockBean
    PaginationService paginationService;

    public BoardControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("[View] GET 보드 리스트 페이지 - 정상 호출")
    void givenNothing_whenRequestingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given
        given(boardService.searchBoards(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(model().attributeExists("pagination"));

        then(boardService).should().searchBoards(eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("[View] GET 보드 리스트 페이지 - 키워드를 통해 정상 호출")
    void givenSearchKeyword_whenRequestingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given
        String searchKeyword = "title";
        given(boardService.searchBoards(eq(searchKeyword), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(
                        get("/boards")
                                .queryParam("searchKeyword", searchKeyword)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(model().attributeExists("pagination"));

        then(boardService).should().searchBoards(eq(searchKeyword), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("[View] GET 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    void givenPagingAndSortingParams_whenSearchingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(boardService.searchBoards(null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/boards")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(model().attribute("pagination", barNumbers));
        then(boardService).should().searchBoards(null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
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
