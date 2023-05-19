package com.pinterest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@DisplayName("View 컨트롤러 - 보드")
class BoardControllerTest {

    private final MockMvc mvc;

    public BoardControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("[View] GET 보드 리스트 페이지 - 정상 호출")
    void givenNothing_whenRequestingBoardsView_thenReturnsBoardsView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/index"))
                .andExpect(model().attributeExists("boards"));
    }

    @Test
    @DisplayName("[View] GET 보드 상세 페이지 - 정상 호출")
    void givenNothing_whenRequestBoardView_thenReturnBoardView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/boards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("boards/detail"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attributeExists("articles"));

    }
}
