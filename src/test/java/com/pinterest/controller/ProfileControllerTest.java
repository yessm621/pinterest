package com.pinterest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@DisplayName("View 컨트롤러 - 프로필")
class ProfileControllerTest {

    private final MockMvc mvc;

    public ProfileControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("[View] GET 프로필 페이지 - 정상 호출")
    void givenNoting_whenRequestingProfileView_thenReturnProfileView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("profile"));
    }
}
