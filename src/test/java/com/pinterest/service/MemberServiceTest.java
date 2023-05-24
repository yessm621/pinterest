package com.pinterest.service;

import com.pinterest.domain.Member;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 프로필")
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
        MemberDto dto = sut.getMember(memberId);

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

    @Test
    @DisplayName("사용자에 대한 프로필 정보를 입력하면, 프로필을 수정한다.")
    void givenMemberInfo_whenUpdatingMember_thenUpdatesMember() {
        // Given
        Member member = createMember();
        MemberDto dto = createMemberDto();
        given(memberRepository.getReferenceById(dto.getId())).willReturn(member);

        // When
        sut.updateMember(dto);

        // Then
        assertThat(member).hasFieldOrPropertyWithValue("nickname", dto.getNickname());
        assertThat(member).hasFieldOrPropertyWithValue("description", dto.getDescription());
        assertThat(member).hasFieldOrPropertyWithValue("image", dto.getImage());
        then(memberRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("없는 사용자에 대한 프로필 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    void givenNoneExistenceMemberInfo_whenUpdatingMember_thenLogsWarningAndDoesNothing() {
        // Given
        MemberDto dto = createMemberDto();
        given(memberRepository.getReferenceById(dto.getId())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateMember(dto);

        // Then
        then(memberRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("사용자 ID를 입력하면, 사용자를 삭제한다.")
    void givenArticleId_whenDeletingMember_thenDeletesMember() {
        // Given
        Long articleId = 1L;
        willDoNothing().given(memberRepository).deleteById(articleId);

        // When
        sut.deleteMember(articleId);

        // Then
        then(memberRepository).should().deleteById(articleId);
    }

    private Member createMember() {
        return Member.of(
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "승미입니다.",
                "image"
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
