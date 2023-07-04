package com.pinterest.controller;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.request.BoardRequest;
import com.pinterest.dto.response.BoardResponse;
import com.pinterest.service.BoardService;
import com.pinterest.service.PaginationService;
import com.pinterest.util.FormDataEncoder;
import com.pinterest.util.TestSecurityConfig;
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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@DisplayName("View 컨트롤러 - 보드")
class BoardControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    BoardService boardService;

    @MockBean
    PaginationService paginationService;

    public BoardControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
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
    @WithMockUser
    @DisplayName("[View] GET 보드 상세 페이지 - 정상 호출, 인증된 사용자")
    void givenNothing_whenRequestBoardView_thenReturnBoardView() throws Exception {
        // Given
        Long boardId = 1L;
        given(boardService.getBoardWithArticles(boardId)).willReturn(createBoardWithArticleDto());

        // When & Then
        mvc.perform(get("/boards/" + boardId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/detail"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attributeExists("articles"));
        then(boardService).should().getBoardWithArticles(boardId);
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
    @WithMockUser
    @DisplayName("[View] GET 새 보드 작성 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    void givenNothing_whenRequesting_thenReturnsNewBoardPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/boards/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/form"));
    }

    @WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("[View] POST 새 보드 등록 - 정상 호출")
    void givenNewBoardInfo_whenRequesting_thenSavesNewBoard() throws Exception {
        // Given
        BoardRequest boardRequest = BoardRequest.of("title", "image");
        willDoNothing().given(boardService).saveBoard(any(BoardDto.class));

        // When & Then
        mvc.perform(
                        post("/boards/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(boardRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/boards"))
                .andExpect(redirectedUrl("/boards"));
        then(boardService).should().saveBoard(any(BoardDto.class));
    }

    @Test
    @DisplayName("[View] GET 보드 수정 페이지 - 인증 없을 땐 로그인 페이지로 이동")
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
    @WithMockUser
    @DisplayName("[View] POST 보드 수정 페이지 - 정상 호출, 인증된 사용자")
    void givenNothing_whenRequesting_thenReturnsUpdatedBoardPage() throws Exception {
        // Given
        Long boardId = 1L;
        BoardDto dto = createBoardDto();
        given(boardService.getBoard(boardId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/boards/" + boardId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/updateForm"))
                .andExpect(model().attribute("board", BoardResponse.from(dto)));
        then(boardService).should().getBoard(boardId);
    }

    @Test
    @WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[View] POST 보드 수정 - 정상 호출")
    void givenUpdatedBoardInfo_whenRequesting_thenUpdatesNewBoard() throws Exception {
        // Given
        Long boardId = 1L;
        BoardRequest boardRequest = BoardRequest.of("title", "image");
        willDoNothing().given(boardService).updateBoard(eq(boardId), any(BoardDto.class));

        // When & Then
        mvc.perform(
                        post("/boards/" + boardId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(boardRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/boards/" + boardId))
                .andExpect(redirectedUrl("/boards/" + boardId));
        then(boardService).should().updateBoard(eq(boardId), any(BoardDto.class));
    }

    @Test
    @WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
                "title",
                "image"
        );
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
