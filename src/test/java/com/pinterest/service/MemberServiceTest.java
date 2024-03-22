package com.pinterest.service;

import com.pinterest.domain.Member;
import com.pinterest.dto.ProfileDto;
import com.pinterest.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 사용자")
class MemberServiceTest {

    @InjectMocks
    MemberService sut;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("프로필을 조회하면 프로필을 반환한다.")
    void givenMemberId_whenSearchingMember_thenReturnsMember() {
        // Given
        Long memberId = 1L;
        Member member = createMember();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // When
        ProfileDto dto = sut.getMember(memberId);

        // Then
        assertThat(dto).hasFieldOrPropertyWithValue("nickname", dto.getNickname());
        then(memberRepository).should().findById(memberId);
    }

    @Test
    @DisplayName("없는 프로필을 조회하면, 오류를 반환한다.")
    void givenNoneExistenceMemberId_whenSearchingMember_thenThrowsException() {
        // Given
        Long memberId = 0L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sut.getMember(memberId));
        then(memberRepository).should().findById(memberId);
    }

    private Member createMember() {
        return Member.of(
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image"
        );
    }
}
