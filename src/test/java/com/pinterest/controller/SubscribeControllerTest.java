package com.pinterest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscribeController.class)
@DisplayName("View 컨트롤러 - 구독")
class SubscribeControllerTest {

    private final MockMvc mvc;

    public SubscribeControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("[View] GET 구독 리스트 페이지 - 정상 호출")
    void givenNoting_whenRequestingSubscribesView_thenReturnSubscribesView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/subscribes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("subscribes/index"))
                .andExpect(model().attributeExists("boards"));
    }
}
