package com.pinterest.util;

import com.pinterest.config.SecurityConfig;
import com.pinterest.domain.Member;
import com.pinterest.repository.MemberRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private MemberRepository memberRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(Member.of(
                "test@gmail.com",
                "pw",
                "test-nickname",
                "test image"
        )));
    }
}
