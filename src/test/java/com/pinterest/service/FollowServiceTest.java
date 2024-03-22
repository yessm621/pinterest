package com.pinterest.service;

import com.pinterest.domain.Follow;
import com.pinterest.domain.Member;
import com.pinterest.dto.FollowDto;
import com.pinterest.repository.FollowRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.FollowQueryRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 팔로우")
class FollowServiceTest {

    @InjectMocks
    FollowService sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    FollowRepository followRepository;

    @Mock
    FollowQueryRepository followQueryRepository;

    @Test
    @DisplayName("팔로우 정보 가져오기")
    void givenMemberInfo_whenSearchingFollow_thenReturnsFollow() {
        // Given
        given(followQueryRepository.getFollow(anyString(), anyLong())).willReturn(createFollow());

        // When
        sut.getFollow(anyString(), anyLong());

        // Then
        then(followQueryRepository).should().getFollow(anyString(), anyLong());
    }

    @Test
    @DisplayName("팔로우 정보 가져오기 - null 반환")
    void givenMemberInfo_whenSearchingFollow_thenReturnsNull() {
        // Given
        given(followQueryRepository.getFollow(anyString(), anyLong())).willReturn(null);

        // When
        FollowDto dto = sut.getFollow(anyString(), anyLong());

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("팔로워 수 가져오기")
    void givenToMemberId_whenSearchingCountToMember_thenReturnsCountToMember() {
        // Given
        Long toMemberId = 1L;
        Member toMember = createToMember();
        given(memberRepository.findById(toMemberId)).willReturn(Optional.of(toMember));
        given(followRepository.countByToMember(toMember)).willReturn(100L);

        // When
        sut.countToMember(toMemberId);

        // Then
        then(memberRepository).should().findById(toMemberId);
        then(followRepository).should().countByToMember(toMember);
    }

    @Test
    @DisplayName("팔로워 수 가져오기 - 회원정보가 존재하지 않음")
    void givenToMemberId_whenSearchingCountToMember_thenThrowsException() {
        // Given
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sut.countToMember(anyLong()));
        then(memberRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("팔로잉 수 가져오기")
    void givenToMemberId_whenSearchingCountFromMember_thenReturnsCountFromMember() {
        // Given
        Long fromMemberId = 1L;
        Member fromMember = createMember();
        given(memberRepository.findById(fromMemberId)).willReturn(Optional.of(fromMember));
        given(followRepository.countByFromMember(fromMember)).willReturn(100L);

        // When
        sut.countFromMember(fromMemberId);

        // Then
        then(memberRepository).should().findById(fromMemberId);
        then(followRepository).should().countByFromMember(fromMember);
    }

    @Test
    @DisplayName("팔로우하기")
    void givenMemberInfo_whenFollowing_thenReturnsVoid() {
        // Given
        String email = "test@gmail.com";
        Long toMemberId = 1L;
        Member fromMember = createMember();
        Member toMember = createToMember();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(fromMember));
        given(memberRepository.findById(toMemberId)).willReturn(Optional.of(toMember));

        // When
        sut.createFollow(email, toMemberId);

        // Then
        then(memberRepository).should().findByEmail(email);
        then(memberRepository).should().findById(toMemberId);
    }

    @Test
    @DisplayName("팔로우 취소")
    void givenMemberInfo_whenFollowingCancel_thenReturnsVoid() {
        // Given
        Long followId = 1L;
        String loginEmail = "test@gmail.com";
        Follow follow = createFollow();
        given(followRepository.findById(followId)).willReturn(Optional.of(follow));
        willDoNothing().given(followRepository).delete(follow);

        // When
        sut.cancel(followId, loginEmail);

        // Then
        then(followRepository).should().findById(anyLong());
        then(followRepository).should().delete(follow);
    }

    @Test
    @DisplayName("팔로우 취소 - 권한이 없는 경우")
    void givenMemberInfo_whenFollowingCancel_thenThrowsException() {
        // Given
        Long followId = 1L;
        String loginEmail = "test2@gmail.com";
        Follow follow = createFollow();
        given(followRepository.findById(followId)).willReturn(Optional.of(follow));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sut.cancel(followId, loginEmail));
        then(followRepository).should().findById(anyLong());
    }

    private Follow createFollow() {
        return Follow.of(
                createMember(),
                createToMember()
        );
    }

    private Member createMember() {
        return Member.of(
                "test@gmail.com",
                "test123",
                "test",
                "image"
        );
    }

    private Member createToMember() {
        return Member.of(
                "test2@gmail.com",
                "test123",
                "test2",
                "image"
        );
    }
}