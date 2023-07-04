package com.pinterest.controller;

import com.pinterest.dto.MemberDto;
import com.pinterest.service.MemberService;
import com.pinterest.util.FormDataEncoder;
import com.pinterest.util.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@DisplayName("View 컨트롤러 - 회원")
class MemberControllerTest {

    private final MockMvc mvc;

    @MockBean
    private MemberService memberService;

    public MemberControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @WithMockUser
    @DisplayName("[View] GET 프로필 페이지 - 정상 호출")
    void givenNoting_whenRequestingProfileView_thenReturnProfileView() throws Exception {
        // Given
        given(memberService.getMember(anyLong())).willReturn(createMemberDto());

        // When & Then
        mvc.perform(get("/members/" + anyLong()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("profile"));
        then(memberService).should().getMember(anyLong());
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
