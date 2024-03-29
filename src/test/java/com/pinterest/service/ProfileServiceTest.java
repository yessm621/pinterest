package com.pinterest.service;

import com.pinterest.domain.FileEntity;
import com.pinterest.domain.Member;
import com.pinterest.dto.MemberDto;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 프로필")
class ProfileServiceTest {

    @InjectMocks
    ProfileService sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    FileService fileService;

    @Test
    @DisplayName("사용자에 대한 프로필 정보를 입력하면, 프로필을 수정한다.")
    void givenMemberInfo_whenUpdatingMember_thenUpdatesMember() {
        // Given
        Member member = createMember();
        MemberDto dto = createMemberDto();
        MockMultipartFile file = new MockMultipartFile("fileName", "test.png", "image/*", "test file".getBytes(StandardCharsets.UTF_8));
        given(memberRepository.getReferenceById(dto.getId())).willReturn(member);
        given(fileService.upload(file)).willReturn(any(FileEntity.class));

        // When
        sut.updateMember(dto.getId(), dto, file);

        // Then
        assertThat(member).hasFieldOrPropertyWithValue("nickname", dto.getNickname());
        assertThat(member).hasFieldOrPropertyWithValue("image", dto.getImage());
        then(memberRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("없는 사용자에 대한 프로필 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    void givenNoneExistenceMemberInfo_whenUpdatingMember_thenLogsWarningAndDoesNothing() {
        // Given
        MemberDto dto = createMemberDto();
        MockMultipartFile file = new MockMultipartFile("fileName", "test.png", "image/*", "test file".getBytes(StandardCharsets.UTF_8));
        given(memberRepository.getReferenceById(dto.getId())).willThrow(PinterestException.class);

        // When
        sut.updateMember(dto.getId(), dto, file);

        // Then
        then(memberRepository).should().getReferenceById(dto.getId());
    }

    private Member createMember() {
        return Member.of(
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image"
        );
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}