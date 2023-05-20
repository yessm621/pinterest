package com.pinterest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Data REST - API 테스트")
public class DataRestTest {

    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("[API] 보드 리스트 조회")
    void givenNoting_whenRequestingBoards_thenReturnsBoardsJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @Test
    @DisplayName("[API] 보드 -> 게시글 리스트 조회")
    void givenNothing_whenRequestingArticlesFromBoard_thenReturnsArticlesFromBoardJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @Test
    @DisplayName("[API] 게시글 리스트 조회")
    void givenNoting_whenRequestingArticles_thenReturnsArticlesJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @Test
    @DisplayName("[API] 게시글 -> 댓글 리스트 조회")
    void givenNothing_whenRequestingCommentsFromArticle_thenReturnsCommentsFromArticleJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @Test
    @DisplayName("[API] 댓글 리스트 조회")
    void givenNoting_whenRequestingComments_thenReturnsCommentsJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @Test
    @DisplayName("[API] 프로필 조회")
    void givenNoting_whenRequestingProfile_thenReturnsProfileJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }
}
